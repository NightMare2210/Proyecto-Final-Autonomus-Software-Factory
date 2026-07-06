# Data Model: [US-05] Estado en Tiempo Real de Propiedades

## Entidades del dominio

### Propiedad (Domain Entity)

Representa una unidad inmobiliaria gestionada en el sistema.

| Campo | Tipo | Restricciones | Descripción |
|-------|------|---------------|-------------|
| `id` | `String` (UUID) | NOT NULL, inmutable | Identificador único de la propiedad |
| `nombre` | `String` | NOT NULL, 1–150 chars | Nombre o título de la propiedad |
| `descripcion` | `String` | NOT NULL, 1–2000 chars | Descripción completa de la propiedad |
| `precio` | `BigDecimal` | NOT NULL, > 0 | Precio de lista en moneda local |
| `estado` | `EstadoPropiedad` | NOT NULL | Estado comercial actual |
| `ultimaActualizacion` | `Instant` | NOT NULL | Timestamp del último cambio de estado |

**Reglas de dominio**:
- El `id` se genera en el momento de creación y no puede ser modificado.
- `ultimaActualizacion` DEBE actualizarse automáticamente cada vez que `estado` cambia.
- Una `Propiedad` con estado `VENDIDA` es un estado terminal — no puede volver a `DISPONIBLE` ni a `RESERVADA`.
- Una `Propiedad` con estado `RESERVADA` puede volver a `DISPONIBLE` (si la reserva caduca).

---

### EstadoPropiedad (Value Object / Enum)

| Valor | Descripción | Transiciones permitidas |
|-------|-------------|------------------------|
| `DISPONIBLE` | La propiedad está libre para visita y contacto | → `RESERVADA`, → `VENDIDA` |
| `RESERVADA` | La propiedad tiene una reserva activa | → `DISPONIBLE`, → `VENDIDA` |
| `VENDIDA` | La propiedad fue transferida — estado terminal | (ninguna) |

---

### PropiedadEstadoCambiado (Domain Event)

Evento de dominio emitido cada vez que el estado de una propiedad cambia. No se persiste —
es el mecanismo de notificación hacia el adaptador SSE.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `propiedadId` | `String` | ID de la propiedad cuyo estado cambió |
| `nuevoEstado` | `EstadoPropiedad` | Nuevo valor del estado |
| `timestamp` | `Instant` | Momento exacto del cambio |

---

## Puertos del dominio (interfaces)

### Puertos de entrada (Use Cases — `domain/port/in/`)

```java
// Puerto 1: Consultar una propiedad
public interface ObtenerPropiedadUseCase {
    Propiedad obtener(String id);
}

// Puerto 2: Cambiar el estado de una propiedad
public interface CambiarEstadoPropiedadUseCase {
    Propiedad cambiarEstado(String id, EstadoPropiedad nuevoEstado);
}
```

### Puertos de salida (Adapters — `domain/port/out/`)

```java
// Puerto 3: Persistencia
public interface PropiedadRepositoryPort {
    Optional<Propiedad> findById(String id);
    Propiedad save(Propiedad propiedad);
}

// Puerto 4: Notificación en tiempo real
public interface PropiedadEventPublisherPort {
    void publicar(PropiedadEstadoCambiado evento);
}
```

---

## Diagrama de capas (Clean Architecture)

```text
┌─────────────────────────────────────────────────────┐
│                   DOMAIN                            │
│  Propiedad · EstadoPropiedad · PropiedadEstadoCambiado │
│  ObtenerPropiedadUseCase (port.in)                  │
│  CambiarEstadoPropiedadUseCase (port.in)             │
│  PropiedadRepositoryPort (port.out)                 │
│  PropiedadEventPublisherPort (port.out)             │
└────────────────────────────┬────────────────────────┘
                             │ depende de (hacia adentro)
┌────────────────────────────▼────────────────────────┐
│                 APPLICATION                         │
│  ObtenerPropiedadService (implementa port.in)       │
│  CambiarEstadoPropiedadService (implementa port.in) │
└────────────────────────────┬────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────┐
│               INFRASTRUCTURE                        │
│  adapter/in/web/                                    │
│    PropiedadController     (adaptador REST entrante) │
│    PropiedadEstadoController (adaptador REST advisor)│
│  adapter/out/persistence/                           │
│    PropiedadJpaEntity                               │
│    PropiedadJpaRepository (Spring Data)             │
│    PropiedadRepositoryAdapter (implementa port.out) │
│  adapter/out/sse/                                   │
│    SsePropiedadEmitterAdapter (implementa port.out) │
│    PropiedadSseEmitterRegistry                      │
└─────────────────────────────────────────────────────┘
```

**Regla de dependencia**: Las flechas solo apuntan hacia adentro.
`infrastructure` conoce `application` y `domain`; `domain` no conoce a nadie.

---

## Esquema de persistencia JPA

```sql
CREATE TABLE propiedad (
  id                  VARCHAR(36)    PRIMARY KEY,
  nombre              VARCHAR(150)   NOT NULL,
  descripcion         VARCHAR(2000)  NOT NULL,
  precio              DECIMAL(15,2)  NOT NULL,
  estado              VARCHAR(20)    NOT NULL CHECK (estado IN ('DISPONIBLE','RESERVADA','VENDIDA')),
  ultima_actualizacion TIMESTAMP     NOT NULL
);
```

La tabla se genera automáticamente por Hibernate (H2 en-memory); en entornos productivos
debe gestionarse con Flyway o Liquibase (fuera del alcance del MVP).

---

## Transiciones de estado — máquina de estados

```text
                   ┌─────────────┐
       ┌──────────▶│  DISPONIBLE │◀──────────┐
       │           └──────┬──────┘           │
       │                  │                  │ (reserva caduca)
       │                  ▼                  │
       │           ┌─────────────┐           │
       │           │  RESERVADA  │───────────┘
       │           └──────┬──────┘
       │                  │
       │                  ▼
       │           ┌─────────────┐
       └───────────│   VENDIDA   │  (estado terminal, sin salida)
                   └─────────────┘
```

La validación de transición DEBE estar en el dominio (`Propiedad.cambiarEstado(nuevo)`),
no en el controlador ni en la capa de persistencia.

# Quickstart: [US-05] Estado en Tiempo Real de Propiedades

## Propósito

Guía de validación ejecutable para verificar que la feature funciona de extremo a extremo
antes de continuar al siguiente ciclo de desarrollo. Cada escenario referencia los criterios de
aceptación definidos en `spec.md`.

---

## Prerrequisitos

| Herramienta | Versión mínima | Verificación |
|-------------|----------------|-------------|
| Java | 17 | `java --version` |
| Gradle | Wrapper incluido | `./gradlew --version` |
| curl | cualquier | `curl --version` |

> El proyecto usa H2 en memoria — no requiere base de datos externa.

---

## Datos precargados

Al arrancar la aplicación, `src/main/resources/db/data.sql` carga automáticamente
tres propiedades de muestra — no se requiere ningún paso de seed manual:

| UUID | Estado inicial |
|------|----------------|
| `3fa85f64-5717-4562-b3fc-2c963f66afa6` | DISPONIBLE |
| `b2e8c4a1-1234-5678-9abc-def012345678` | RESERVADA |
| `c9f1d2e3-abcd-ef01-2345-6789abcdef01` | VENDIDA |

## Arrancar la aplicación

```bash
./gradlew bootRun
```

La aplicación levanta en `http://localhost:8080`. Esperar el log:
```
Started CitasaludServiceApplication in X.XXX seconds
```

Los datos del `data.sql` están disponibles inmediatamente — no se necesita ningún curl previo.

---

## Escenario 1 — Consultar el estado actual de una propiedad

**Cubre**: FR-001, FR-002, SC-004

```bash
# Propiedad DISPONIBLE — precargada en data.sql
PROPIEDAD_ID="3fa85f64-5717-4562-b3fc-2c963f66afa6"

# Consultar la ficha de la propiedad
curl -s http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID | jq .
```

**Resultado esperado**:
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nombre": "Apartamento Sector Norte — Piso 4",
  "estado": "DISPONIBLE",
  "accionesPermitidas": {
    "puedeAgendarVisita": true,
    "puedeContactar": true
  }
}
```

---

## Escenario 2 — Cambio de estado se propaga en tiempo real (< 60 s)

**Cubre**: FR-001, SC-001 — criterio de aceptación US1

### Paso A: Abrir el stream SSE en terminal 1

```bash
PROPIEDAD_ID="3fa85f64-5717-4562-b3fc-2c963f66afa6"
curl -N http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID/estado/stream
```

Deja esta terminal abierta. Verás el prompt bloqueado — esto es correcto para SSE.

### Paso B: Cambiar el estado desde terminal 2 (simula al asesor)

```bash
PROPIEDAD_ID="3fa85f64-5717-4562-b3fc-2c963f66afa6"
curl -s -X PUT http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "RESERVADA"}' | jq .
```

**Resultado esperado en terminal 2** (respuesta PUT):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "estado": "RESERVADA",
  "accionesPermitidas": {
    "puedeAgendarVisita": false,
    "puedeContactar": true
  }
}
```

**Resultado esperado en terminal 1** (evento SSE — debe aparecer en < 1 segundo):
```
event: estado-cambiado
data: {"propiedadId":"3fa85f64-5717-4562-b3fc-2c963f66afa6","estado":"RESERVADA","timestamp":"2026-06-27T14:30:00Z"}
```

**Criterio de éxito**: El evento SSE aparece en terminal 1 en menos de 5 segundos tras ejecutar
el PUT (ampliamente dentro del límite de 60 segundos del SLA).

---

## Escenario 3 — Propiedad reservada bloquea agendamiento

**Cubre**: FR-003, FR-004, SC-002 — criterio de aceptación US2

```bash
# Propiedad RESERVADA — precargada en data.sql (no requiere PUT previo)
PROPIEDAD_ID="b2e8c4a1-1234-5678-9abc-def012345678"

# Consultar la ficha — verificar que accionesPermitidas.puedeAgendarVisita = false
curl -s http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID | jq '.accionesPermitidas'
```

**Resultado esperado**:
```json
{
  "puedeAgendarVisita": false,
  "puedeContactar": true
}
```

El portal DEBE usar `puedeAgendarVisita: false` para deshabilitar el botón de agendamiento
y mostrar un mensaje informativo al cliente.

---

## Escenario 4 — Propiedad vendida: estado visible, sin opciones de contacto

**Cubre**: FR-005, FR-007, SC-004 — criterio de aceptación US3

```bash
# Propiedad VENDIDA — precargada en data.sql (no requiere PUT previo)
PROPIEDAD_ID="c9f1d2e3-abcd-ef01-2345-6789abcdef01"

curl -s http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID | jq .
```

**Resultado esperado**:
```json
{
  "estado": "VENDIDA",
  "accionesPermitidas": {
    "puedeAgendarVisita": false,
    "puedeContactar": false
  }
}
```

---

## Escenario 5 — Transición ilegal es rechazada

**Cubre**: reglas de dominio (VENDIDA → DISPONIBLE no permitida)

```bash
# Misma propiedad VENDIDA del escenario anterior
PROPIEDAD_ID="c9f1d2e3-abcd-ef01-2345-6789abcdef01"

# Intentar revertir una propiedad VENDIDA a DISPONIBLE
curl -s -X PUT http://localhost:8080/api/v1/propiedades/$PROPIEDAD_ID/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "DISPONIBLE"}' | jq .
```

**Resultado esperado** (HTTP 400):
```json
{
  "codigo": "TRANSICION_ESTADO_INVALIDA",
  "mensaje": "No es posible cambiar de VENDIDA a DISPONIBLE"
}
```

---

## Ejecutar suite de pruebas completa

```bash
# Pruebas unitarias + integración + cobertura JaCoCo
./gradlew test jacocoTestReport jacocoTestCoverageVerification

# Pruebas funcionales (Cucumber)
./gradlew cucumberTest

# Verificación de arquitectura (ArchUnit)
./gradlew test --tests "*ArchitectureTest"
```

**Reporte JaCoCo**: `build/reports/jacoco/test/html/index.html`

**Criterio de éxito**:
- `BUILD SUCCESSFUL` sin fallos
- Cobertura global ≥ 80 %
- Cobertura por clase > 80 %

---

## Referencias

- Contrato OpenAPI: [`contracts/openapi.yml`](contracts/openapi.yml)
- Modelo de datos: [`data-model.md`](data-model.md)
- Decisiones técnicas: [`research.md`](research.md)
- Especificación: [`spec.md`](spec.md)

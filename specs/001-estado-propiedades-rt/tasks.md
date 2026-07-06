---
description: "Task list for [US-05] Estado en Tiempo Real de Propiedades"
---

# Tasks: [US-05] Estado en Tiempo Real de Propiedades

**Input**: Design documents from `specs/001-estado-propiedades-rt/`

**Prerequisites**: plan.md ✅ · spec.md ✅ · research.md ✅ · data-model.md ✅ · contracts/openapi.yml ✅

**Tests**: MANDATORY — BDD (Principio II de la Constitución). Tests escritos antes de la
implementación (red → green → refactor). Unit + integration + functional (Cucumber).

**Cambios vs versión anterior**: Resueltos C1 (FR-007 listing), C2 (FR-006 boundary), H1 (contrato
incompleto), H3 (edge cases sin tareas), M2 (SC-001 SLA), M3 (T037/T041 condicionales → reemplazados),
M4 (mapper faltante), L1/L2 (tareas redundantes eliminadas). Total: 50 tasks.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Puede correr en paralelo (archivos distintos, sin dependencias)
- **[Story]**: US a la que pertenece (US1, US2, US3)
- Incluye rutas de archivo desde la raíz del repositorio

## Path Conventions

- Código: `src/main/java/org/ups/citasaludservice/`
- Pruebas: `src/test/java/org/ups/citasaludservice/`
- Recursos prueba: `src/test/resources/`
- Recursos main: `src/main/resources/`
- DB init: `src/main/resources/db/`
- OpenAPI: `src/main/resources/openapi/openapi.yml`

---

## Phase 1: Setup

**Propósito**: Dependencias, plugins, contrato OpenAPI actualizado y archivos de DB.
Sin código de negocio.

- [X] T001 Agregar dependencias de Cucumber 7.x, Mockito y ArchUnit a `build.gradle` bajo `testImplementation`
- [X] T002 Agregar el plugin `org.openapi.generator` v7.x a `build.gradle`; configurar `openApiGenerate` apuntando a `src/main/resources/openapi/openapi.yml`, generador `spring`, output en `build/generated/`, incluir `PropiedadResumenResponse` en el modelo generado
- [X] T003 Copiar `specs/001-estado-propiedades-rt/contracts/openapi.yml` (ya incluye endpoint `GET /api/v1/propiedades` y schema `PropiedadResumenResponse`) a `src/main/resources/openapi/openapi.yml`
- [X] T004 [P] Agregar plugin `jacoco` a `build.gradle`; configurar `jacocoTestReport` (XML + HTML) y `jacocoTestCoverageVerification` con umbral global ≥ 80 % y regla `element=CLASS` con umbral > 80 %; excluir explícitamente paquete `build.generated` de las métricas
- [X] T005 [P] Crear estructura de paquetes Clean Architecture en `src/main/java/org/ups/citasaludservice/`: `domain/model/`, `domain/event/`, `domain/port/in/`, `domain/port/out/`, `application/usecase/`, `infrastructure/adapter/in/web/`, `infrastructure/adapter/out/persistence/`, `infrastructure/adapter/out/sse/`, `infrastructure/config/`
- [X] T006 [P] Crear estructura de paquetes de prueba en `src/test/java/org/ups/citasaludservice/`: `unit/domain/`, `unit/application/`, `integration/`, `functional/steps/`, `architecture/`
- [X] T007 Crear `src/main/resources/db/schema.sql`: `CREATE TABLE IF NOT EXISTS propiedad (id VARCHAR(36) PRIMARY KEY, nombre VARCHAR(150) NOT NULL, descripcion VARCHAR(2000) NOT NULL, precio DECIMAL(15,2) NOT NULL, estado VARCHAR(20) NOT NULL CHECK (estado IN ('DISPONIBLE','RESERVADA','VENDIDA')), ultima_actualizacion TIMESTAMP NOT NULL)`
- [X] T008 [P] Crear `src/main/resources/db/data.sql` con 3 INSERTs: UUID `3fa85f64-5717-4562-b3fc-2c963f66afa6` → DISPONIBLE; UUID `b2e8c4a1-1234-5678-9abc-def012345678` → RESERVADA; UUID `c9f1d2e3-abcd-ef01-2345-6789abcdef01` → VENDIDA
- [X] T009 Actualizar `src/main/resources/application.yaml`: `spring.datasource` H2, `spring.sql.init.schema-locations: classpath:db/schema.sql`, `spring.sql.init.data-locations: classpath:db/data.sql`, `spring.sql.init.mode: always`, `spring.jpa.hibernate.ddl-auto: none`, `spring.mvc.async.request-timeout` para SSE (depende T007, T008)

---

## Phase 2: Foundational

**⚠️ CRÍTICO**: Completar antes de cualquier User Story.

- [X] T010 Crear `CucumberRunner.java` en `src/test/java/org/ups/citasaludservice/functional/` con `@Suite`, `@IncludeEngines("cucumber")` y `@ConfigurationParameter` para glue package y features path (`src/test/resources/features`)
- [X] T011 [P] Crear `ArchitectureTest.java` en `src/test/java/org/ups/citasaludservice/architecture/` con reglas ArchUnit: `domain` no importa `infrastructure` ni `application`; `application` no importa `infrastructure`; controladores web solo dependen de `domain.port.in`
- [X] T012 [P] Crear `BeanConfiguration.java` en `infrastructure/config/` con `@Bean` que conectan puertos de entrada (use cases) con implementaciones en `application/usecase/` y puertos de salida con adaptadores en `infrastructure/adapter/out/`

**Checkpoint**: Proyecto compila, paquetes listos, DB init configurada, Cucumber runner y ArchUnit activos.

---

## Phase 3: User Story 1 — Actualización automática de estado + listado (Priority: P1) 🎯 MVP

**Goal**: Asesor cambia estado → SSE propaga en < 1 s. API expone GET ficha, GET listado (FR-007),
PUT estado y stream SSE. Datos precargados en `data.sql`.

**Independent Test**: `./gradlew bootRun` → `GET /api/v1/propiedades` retorna 3 propiedades con
sus estados; stream SSE recibe evento tras PUT. (quickstart escenarios 1 y 2)

### Pruebas US1 — ESCRIBIR PRIMERO, confirmar FALLAN ⚠️

- [X] T013 [P] [US1] Crear `PropiedadTest.java` en `unit/domain/` con pruebas Given/When/Then: creación con estado inicial DISPONIBLE; transición DISPONIBLE → RESERVADA válida; `cambiarEstado(VENDIDA)` desde VENDIDA lanza excepción de dominio; `accionesPermitidas()` retorna `puedeAgendarVisita=true, puedeContactar=true` para DISPONIBLE; `puedeAgendarVisita=false` para RESERVADA; ambas `false` para VENDIDA
- [X] T014 [P] [US1] Crear `CambiarEstadoPropiedadServiceTest.java` en `unit/application/` mockeando `PropiedadRepositoryPort` y `PropiedadEventPublisherPort`; verifica que el use case persiste el nuevo estado y publica `PropiedadEstadoCambiado`
- [X] T015 [P] [US1] Crear `ObtenerPropiedadServiceTest.java` en `unit/application/` mockeando `PropiedadRepositoryPort`; verifica devolución correcta de entidad y excepción cuando id no existe
- [X] T016 [P] [US1] Crear `PropiedadControllerIntegrationTest.java` en `integration/` con `@SpringBootTest` + `MockMvc`: `GET /api/v1/propiedades` → 200 con 3 items de data.sql; `GET /api/v1/propiedades/{id}` → 200; `PUT /api/v1/propiedades/{id}/estado` → 200; transición VENDIDA → cualquier cosa → 400; id inexistente → 404
- [X] T017 [P] [US1] Crear `src/test/resources/features/estado_propiedad.feature` con escenarios Gherkin US1: cambio de estado visible en portal en < 60 s; cambio reflejado en SSE; reversión de reserva; listado muestra estado de cada propiedad

### Implementación US1

- [X] T018 [P] [US1] Crear `EstadoPropiedad.java` enum en `domain/model/` con valores `DISPONIBLE`, `RESERVADA`, `VENDIDA`; método `transicionesPermitidas()` que retorna: DISPONIBLE → {RESERVADA, VENDIDA}; RESERVADA → {DISPONIBLE, VENDIDA}; VENDIDA → {} (conjunto vacío — estado terminal)
- [X] T019 [P] [US1] Crear `PropiedadEstadoCambiado.java` record en `domain/event/` con campos `String propiedadId`, `EstadoPropiedad nuevoEstado`, `Instant timestamp`
- [X] T020 [P] [US1] Crear `ObtenerPropiedadUseCase.java` interface en `domain/port/in/` con métodos `Propiedad obtener(String id)` y `List<Propiedad> listar()`
- [X] T021 [P] [US1] Crear `CambiarEstadoPropiedadUseCase.java` interface en `domain/port/in/` con método `Propiedad cambiarEstado(String id, EstadoPropiedad nuevoEstado)`
- [X] T022 [P] [US1] Crear `PropiedadRepositoryPort.java` interface en `domain/port/out/` con métodos `Optional<Propiedad> findById(String id)`, `List<Propiedad> findAll()` y `Propiedad save(Propiedad p)`
- [X] T023 [P] [US1] Crear `PropiedadEventPublisherPort.java` interface en `domain/port/out/` con método `void publicar(PropiedadEstadoCambiado evento)`
- [X] T024 [US1] Crear `Propiedad.java` entity en `domain/model/` con campos `id`, `nombre`, `descripcion`, `precio`, `estado`, `ultimaActualizacion`; método `cambiarEstado(EstadoPropiedad nuevo)` que llama `estado.transicionesPermitidas()`, lanza `EstadoTransicionInvalidaException` si `nuevo` no está en el set, actualiza `estado` y `ultimaActualizacion`; método `accionesPermitidas()` que retorna record/objeto con `puedeAgendarVisita = (estado == DISPONIBLE)` y `puedeContactar = (estado != VENDIDA)` (depende T018–T023)
- [X] T025 [US1] Crear `ObtenerPropiedadService.java` en `application/usecase/` implementando `ObtenerPropiedadUseCase`; delega a `PropiedadRepositoryPort.findById()` y `findAll()` (depende T024)
- [X] T026 [US1] Crear `CambiarEstadoPropiedadService.java` en `application/usecase/` implementando `CambiarEstadoPropiedadUseCase`; llama `findById` → `cambiarEstado` → `save` → `publicar` (depende T024)
- [X] T027 [P] [US1] Crear `PropiedadJpaEntity.java` en `infrastructure/adapter/out/persistence/` con `@Entity @Table(name="propiedad")`, campos mapeando `schema.sql` (id, nombre, descripcion, precio, estado como `@Enumerated(STRING)`, ultimaActualizacion)
- [X] T028 [US1] Crear `PropiedadJpaRepository.java` en `infrastructure/adapter/out/persistence/` extendiendo `JpaRepository<PropiedadJpaEntity, String>` — `findAll()` heredado de JpaRepository (depende T027)
- [X] T029 [US1] Crear `PropiedadRepositoryAdapter.java` en `infrastructure/adapter/out/persistence/` implementando `PropiedadRepositoryPort`; convierte `PropiedadJpaEntity` ↔ `Propiedad` mediante mapper inline; implementa `findById`, `findAll` y `save` (depende T028, T024)
- [X] T030 [P] [US1] Crear `PropiedadSseEmitterRegistry.java` en `infrastructure/adapter/out/sse/` como `@Component`; mantiene `ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>>`; expone `agregar(id, emitter)` y `notificar(id, evento)` — en `notificar`, elimina emitters muertos antes de enviar
- [X] T031 [US1] Crear `SsePropiedadEmitterAdapter.java` en `infrastructure/adapter/out/sse/` implementando `PropiedadEventPublisherPort`; serializa `PropiedadEstadoCambiado` a JSON y delega a `PropiedadSseEmitterRegistry.notificar()` (depende T030)
- [X] T032 [US1] Crear `PropiedadMapper.java` en `infrastructure/adapter/in/web/` con métodos estáticos: `toResponse(Propiedad p): PropiedadResponse` y `toResumen(Propiedad p): PropiedadResumenResponse`; mapea `accionesPermitidas()` a los campos del DTO; no contiene lógica de negocio (depende T024)
- [X] T033 [US1] Crear `PropiedadController.java` en `infrastructure/adapter/in/web/` con `@RestController`: `GET /api/v1/propiedades` → llama `ObtenerPropiedadUseCase.listar()` + `PropiedadMapper.toResumen()`; `GET /api/v1/propiedades/{id}` → llama `obtener()` + `toResponse()` (depende T025, T032)
- [X] T034 [US1] Crear `PropiedadEstadoController.java` en `infrastructure/adapter/in/web/` con `PUT /api/v1/propiedades/{id}/estado` → llama `CambiarEstadoPropiedadUseCase`; `GET /api/v1/propiedades/{id}/estado/stream` → crea `SseEmitter`, lo registra en registry, retorna con `MediaType.TEXT_EVENT_STREAM_VALUE` (depende T026, T031, T030)
- [X] T035 [US1] Crear `EstadoPropiedadSteps.java` en `functional/steps/` con step definitions Cucumber para escenarios US1; usa UUIDs fijos del `data.sql` para las consultas; incluye steps del escenario de listado (depende T017)

**Checkpoint**: US1 completo — `./gradlew test` pasa; SSE emite eventos; quickstart 1 y 2 verificados.

---

## Phase 4: User Story 2 — Bloqueo de agendamiento en propiedades reservadas (Priority: P2)

**Goal**: `GET /api/v1/propiedades/b2e8c4a1-...` (precargada RESERVADA) retorna
`accionesPermitidas.puedeAgendarVisita: false` desde el primer arranque.

**Independent Test**: quickstart escenario 3 sin ningún PUT previo.

### Pruebas US2 — ESCRIBIR PRIMERO, confirmar FALLAN ⚠️

- [X] T036 [P] [US2] Agregar escenarios US2 a `estado_propiedad.feature`: Given RESERVADA, When GET ficha, Then puedeAgendarVisita=false; Given RESERVADA→DISPONIBLE, Then puedeAgendarVisita=true
- [X] T037 [P] [US2] Agregar pruebas a `PropiedadTest.java` (unit/domain/): verificar `accionesPermitidas()` retorna `puedeAgendarVisita=false` para RESERVADA y `true` para DISPONIBLE (tests deben fallar hasta que T024 esté implementado)

### Implementación US2

- [X] T038 [US2] Agregar step definitions US2 a `EstadoPropiedadSteps.java` en `functional/steps/` usando UUID RESERVADA del `data.sql` (depende T035, T036)

**Checkpoint**: puedeAgendarVisita refleja RESERVADA correctamente. Datos precargados lo demuestran sin setup.

---

## Phase 5: User Story 3 — Propiedad vendida: estado visible, sin opciones de contacto (Priority: P3)

**Goal**: `GET /api/v1/propiedades/c9f1d2e3-...` (precargada VENDIDA) retorna ambas flags `false`;
cualquier PUT de cambio de estado → HTTP 400.

**Independent Test**: quickstart escenarios 4 y 5.

### Pruebas US3 — ESCRIBIR PRIMERO, confirmar FALLAN ⚠️

- [X] T039 [P] [US3] Agregar escenarios US3 a `estado_propiedad.feature`: Given VENDIDA, When GET ficha, Then puedeContactar=false Y puedeAgendarVisita=false; Given VENDIDA, When PUT cualquier estado, Then HTTP 400 con codigo TRANSICION_ESTADO_INVALIDA
- [X] T040 [P] [US3] Agregar pruebas a `PropiedadTest.java` (unit/domain/): ambas flags `false` para VENDIDA; `cambiarEstado(DISPONIBLE)` lanza excepción cuando estado es VENDIDA

### Implementación US3

- [X] T041 [US3] Crear `EstadoTransicionInvalidaException.java` en `domain/model/` como excepción de dominio sin checar (extends RuntimeException) con campos `estadoActual` y `estadoDestino`; asegurarse de que `Propiedad.cambiarEstado()` (T024) la lanza cuando `nuevo` no está en `transicionesPermitidas()`
- [X] T042 [US3] Crear `GlobalExceptionHandler.java` en `infrastructure/adapter/in/web/` con `@RestControllerAdvice`; captura `EstadoTransicionInvalidaException` → HTTP 400 `ErrorResponse(codigo: "TRANSICION_ESTADO_INVALIDA")`; captura `PropiedadNoEncontradaException` → HTTP 404
- [X] T043 [US3] Agregar step definitions US3 a `EstadoPropiedadSteps.java` (depende T038, T039)

**Checkpoint**: Las 3 US completas; quickstart 1–5 verificados con datos precargados.

---

## Phase Final: Polish, Edge Cases y Métricas de Calidad

**Propósito**: Garantías transversales — edge cases, SLA, arquitectura y cobertura.

### Edge Cases

- [X] T044 [P] EC1 — Resiliencia SSE ante desconexión: actualizar `PropiedadSseEmitterRegistry.agregar()` para enviar el estado actual de la propiedad como primer evento al registrar un nuevo emitter (función "catch-up" al reconectar); agregar test en `PropiedadControllerIntegrationTest` que verifica el evento inicial al suscribirse
- [X] T045 [P] EC2/EC3 — Fresh-state semántica: agregar test en `PropiedadControllerIntegrationTest` que ejecuta `PUT /estado` y luego inmediatamente `GET /propiedades/{id}` verificando que el nuevo estado ya está reflejado (sin caché); documentar en `CambiarEstadoPropiedadService` que el flujo `findById → cambiarEstado → save` garantiza que cada GET subsecuente lee del DB actualizado

### SLA y Cobertura

- [X] T046 [P] SC-001 latencia — Agregar test `SseLatencyTest.java` en `integration/` que ejecuta `PUT /api/v1/propiedades/{id}/estado`, espera el evento SSE con timeout de 2000 ms y aserta que llega antes del timeout; esto valida el SLA en entorno local (cubre SC-001 a nivel de build gate)
- [X] T047 Ejecutar `./gradlew test jacocoTestReport jacocoTestCoverageVerification` y verificar BUILD SUCCESSFUL; cobertura global ≥ 80 % y por clase > 80 %; reporte HTML en `build/reports/jacoco/test/html/index.html`; exclusiones de clases generadas declaradas en `build.gradle`

### Arquitectura y Validación Final

- [X] T048 [P] Ejecutar `./gradlew test` y confirmar que `ArchitectureTest` pasa sin violaciones de capas; ajustar reglas ArchUnit si alguna clase real viola el contrato de dependencias
- [X] T049 Crear `ListarPropiedadesIntegrationTest.java` en `integration/` con `@SpringBootTest` + `MockMvc`: `GET /api/v1/propiedades` → 200 con array de 3 elementos; cada elemento tiene campo `estado` con valor correcto según `data.sql`; propiedad VENDIDA tiene `puedeContactar: false` en la respuesta del listado (cubre FR-007)
- [X] T050 Ejecutar validación completa de `quickstart.md` (escenarios 1–5) con `./gradlew bootRun`; verificar que los 3 UUIDs del `data.sql` funcionan sin ningún step de seed manual

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias; T009 depende de T007/T008
- **Phase 2 (Foundational)**: Requiere Phase 1 — BLOQUEA todo
- **Phase 3 (US1)**: Requiere Phase 2; tests T013–T017 escritos primero en ROJO
- **Phase 4 (US2)**: Requiere checkpoint de US1
- **Phase 5 (US3)**: Requiere checkpoint de US2
- **Polish**: Requiere todas las US completas

### Orden interno US1

```text
Tests (T013–T017) ──[P]──► ROJO primero
     │
     ▼
Enums/Interfaces (T018–T023) ──[P]──► independientes
     │
     ▼
Propiedad entity (T024) ─────────► depende T018–T023
     │
     ├── Use Cases (T025, T026) ──[P]──► dependen T024
     └── JPA Entity (T027) ──[P]──────► independiente
              │
              ▼
         JPA Repo (T028) → Adapter (T029)
     │
     └── SSE Registry (T030) → Adapter (T031)
         │
         ▼
     Mapper (T032) → Controllers (T033, T034) → Steps (T035)
```

### Parallel Opportunities

- Tests de cada US en paralelo (archivos distintos)
- T018–T023 totalmente paralelos (enums, records, interfaces)
- T027 (JPA Entity) paralelo a T025/T026 (use cases)
- T030 (SSE Registry) paralelo a T027–T029 (JPA stack)
- T044, T045, T046, T048 paralelos entre sí (Polish)

---

## Implementation Strategy

### MVP (solo US1)

1. Phase 1 + Phase 2 → base lista con datos precargados
2. Phase 3 → SSE + API completo + listado (FR-007)
3. Validar quickstart 1 y 2 → demo

### Incremental

1. MVP (Phase 1–3) → US1 verificada → demo
2. US2 → UUID RESERVADA sin agendamiento → demo
3. US3 → UUID VENDIDA sin contacto → demo
4. Polish (T044–T050) → edge cases + SLA + ArchUnit + cobertura → release

---

## Notes

- `[P]` = archivos distintos, sin dependencias incompletas
- UUIDs del `data.sql` son referencia fija — no modificar
- DTOs (`PropiedadResponse`, `PropiedadResumenResponse`, `CambiarEstadoRequest`, `EstadoUpdateEvent`, `ErrorResponse`) son **generados** por openapi-generator — no escribir a mano
- `ddl-auto: none` — `schema.sql` es la única fuente de verdad del DDL
- BDD rojo → verde → refactor en cada task de implementación
- `PropiedadMapper` (T032) es el único punto de conversión dominio → DTO; ningún otro componente hace este mapping
- FR-006: garantizado por flag `puedeAgendarVisita` calculado en DB fresh; el portal confirma estado antes de procesar reserva

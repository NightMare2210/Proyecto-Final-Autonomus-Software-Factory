# Implementation Plan: [US-05] Estado en Tiempo Real de Propiedades

**Branch**: `001-estado-propiedades-rt` | **Date**: 2026-06-27 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/001-estado-propiedades-rt/spec.md`

## Summary

Implementar la visualización en tiempo real del estado comercial de una propiedad inmobiliaria
(DISPONIBLE, RESERVADA, VENDIDA) en el portal del cliente. Un asesor actualiza el estado vía API
REST; el cambio se propaga a todos los clientes suscritos mediante Server-Sent Events (SSE) en
menos de 1 segundo, cumpliendo el SLA de 60 segundos del contrato. Las propiedades reservadas
bloquean el agendamiento de visitas; las vendidas ocultan todas las opciones de contacto.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**:
- Spring Boot 4.1.0 (Web MVC, Data JPA, H2)
- Lombok
- JUnit 5 + Spring Boot Test
- Cucumber 7.x + cucumber-junit-platform-engine (pruebas funcionales BDD)
- ArchUnit (validación de arquitectura en tiempo de test)
- JaCoCo Gradle plugin (cobertura ≥ 80 %)
- openapi-generator-gradle-plugin 7.x (generación de stubs y DTOs desde openapi.yml)

**Storage**: H2 in-memory (desarrollo y CI); esquema creado explícitamente vía `src/main/resources/db/schema.sql`; datos de muestra precargados vía `src/main/resources/db/data.sql`; Spring Boot SQL Init con `spring.sql.init.mode: always`; `spring.jpa.hibernate.ddl-auto: none` para que Hibernate no regenere el schema

**Testing**:
- Unit: JUnit 5 + Mockito — prueba de use cases y entidades de dominio en aislamiento
- Integration: Spring Boot Test + MockMvc — prueba de controller + use case + JPA
- Functional: Cucumber 7 + JUnit 5 — escenarios Gherkin Given/When/Then sobre HTTP real

**Target Platform**: Linux server / JVM — Spring Boot fat JAR

**Project Type**: Web service (REST API + SSE)

**Performance Goals**: Latencia SSE < 1 s desde cambio en sistema interno; throughput suficiente
para portal con carga inicial de decenas de usuarios concurrentes

**Constraints**:
- El estado VENDIDA es terminal — la validación de transición vive en el dominio
- Los DTOs del contrato se generan desde `openapi.yml` — no se escriben a mano
- Cobertura JaCoCo: por clase > 80 %, global ≥ 80 % (build-breaking)
- No se introduce Spring WebFlux — se usa `SseEmitter` de Spring MVC

**Scale/Scope**: MVP de nodo único; sin clustering. El registry de `SseEmitter` es in-memory.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Principle | Estado | Detalle |
|------|-----------|--------|---------|
| Architecture | I. Clean Architecture | ✅ PASA | Capas domain → application → infrastructure definidas; domain sin imports de framework |
| Tests | II. BDD Testing Strategy | ✅ PASA | Unit + integration + functional (Cucumber); escenarios Given/When/Then en spec |
| Practices | III. SOLID / YAGNI / DRY | ✅ PASA | Puertos e interfaces segregadas; sin código especulativo; sin duplicación de DTOs |
| Contract | IV. API First | ✅ PASA | `contracts/openapi.yml` existe y se committea antes de implementar |
| Coverage | V. Coverage Quality Gate | ✅ PASA | JaCoCo configurado con umbral por clase > 80 % y global ≥ 80 % |

**Resultado post-diseño**: todos los gates pasan. Sin violaciones que justificar.

## Project Structure

### Documentation (this feature)

```text
specs/001-estado-propiedades-rt/
├── plan.md              ← este archivo
├── research.md          ← decisiones técnicas (stack, real-time, BDD, JaCoCo)
├── data-model.md        ← entidades, puertos, esquema JPA, diagrama de capas
├── quickstart.md        ← guía de validación ejecutable (5 escenarios)
├── contracts/
│   └── openapi.yml      ← contrato OpenAPI 3.0 (fuente de verdad del API)
├── checklists/
│   └── requirements.md  ← checklist de calidad de spec
└── tasks.md             ← pendiente (/speckit-tasks)
```

### Source Code (estructura Clean Architecture)

```text
src/
├── main/
│   ├── java/org/ups/citasaludservice/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Propiedad.java
│   │   │   │   └── EstadoPropiedad.java
│   │   │   ├── event/
│   │   │   │   └── PropiedadEstadoCambiado.java
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       │   ├── ObtenerPropiedadUseCase.java
│   │   │       │   └── CambiarEstadoPropiedadUseCase.java
│   │   │       └── out/
│   │   │           ├── PropiedadRepositoryPort.java
│   │   │           └── PropiedadEventPublisherPort.java
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       ├── ObtenerPropiedadService.java
│   │   │       └── CambiarEstadoPropiedadService.java
│   │   ├── infrastructure/
│   │   │   ├── adapter/
│   │   │   │   ├── in/web/
│   │   │   │   │   ├── PropiedadController.java
│   │   │   │   │   └── PropiedadEstadoController.java
│   │   │   │   └── out/
│   │   │   │       ├── persistence/
│   │   │   │       │   ├── PropiedadJpaEntity.java
│   │   │   │       │   ├── PropiedadJpaRepository.java
│   │   │   │       │   └── PropiedadRepositoryAdapter.java
│   │   │   │       └── sse/
│   │   │   │           ├── PropiedadSseEmitterRegistry.java
│   │   │   │           └── SsePropiedadEmitterAdapter.java
│   │   │   └── config/
│   │   │       └── BeanConfiguration.java
│   │   └── CitasaludServiceApplication.java
│   └── resources/
│       ├── application.yaml
│       ├── db/
│       │   ├── schema.sql               ← DDL: CREATE TABLE propiedad
│       │   └── data.sql                 ← DML: INSERT INTO propiedad (datos de muestra)
│       └── openapi/
│           └── openapi.yml              ← contrato copiado aquí para generación
└── test/
    ├── java/org/ups/citasaludservice/
    │   ├── unit/
    │   │   ├── domain/
    │   │   │   └── PropiedadTest.java
    │   │   └── application/
    │   │       ├── ObtenerPropiedadServiceTest.java
    │   │       └── CambiarEstadoPropiedadServiceTest.java
    │   ├── integration/
    │   │   └── PropiedadControllerIntegrationTest.java
    │   ├── functional/
    │   │   ├── CucumberRunner.java
    │   │   └── steps/
    │   │       └── EstadoPropiedadSteps.java
    │   └── architecture/
    │       └── ArchitectureTest.java
    └── resources/
        └── features/
            └── estado_propiedad.feature
```

**Structure Decision**: Single Spring Boot project. Clean Architecture con paquetes que reflejan
las capas (`domain`, `application`, `infrastructure`). Los adaptadores REST son driving adapters
(`in/web`); JPA y SSE son driven adapters (`out/persistence`, `out/sse`).

## Complexity Tracking

> No aplica — todos los gates de la Constitución pasan sin violaciones.

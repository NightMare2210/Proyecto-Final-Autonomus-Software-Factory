# Research: [US-05] Estado en Tiempo Real de Propiedades

## Stack confirmado (build.gradle)

| Dimensión | Decisión | Fuente |
|-----------|----------|--------|
| Lenguaje | Java 17 | `build.gradle` — `JavaLanguageVersion.of(17)` |
| Framework | Spring Boot 4.1.0 + Spring Web MVC | `build.gradle` — plugin `org.springframework.boot` |
| Persistencia | Spring Data JPA + H2 (in-memory) | `build.gradle` — dependencias JPA + H2 |
| Boilerplate | Lombok | `build.gradle` — `compileOnly 'org.projectlombok:lombok'` |
| Test runner | JUnit 5 (Platform Launcher) | `build.gradle` — `useJUnitPlatform()` |
| Build tool | Gradle | `gradlew`, `build.gradle` |

---

## Decisión 1 — Mecanismo de actualización en tiempo real

**Alternativas evaluadas**:

| Opción | Latencia | Complejidad | Compatibilidad MVC |
|--------|----------|-------------|---------------------|
| Polling HTTP (30 s) | ~30 s p50 | Baja | Alta |
| Server-Sent Events (SSE) | < 1 s | Media | Alta (`SseEmitter`) |
| WebSocket (STOMP) | < 1 s | Alta | Media (requiere config extra) |

**Decisión**: **Server-Sent Events (SSE)** via `SseEmitter` de Spring MVC.

**Rationale**:
- El requisito es unidireccional (servidor → cliente); SSE es el protocolo estándar para este caso.
- `SseEmitter` está disponible en Spring MVC sin dependencias adicionales.
- Latencia prácticamente instantánea — cumple el límite de 60 segundos con margen amplio.
- Más simple que WebSocket; no requiere STOMP ni broker de mensajes.
- Las alternativas descartadas: Polling satisface el SLA pero genera carga innecesaria; WebSocket tiene overhead de protocolo innecesario para comunicación unidireccional.

---

## Decisión 2 — API First: herramienta de generación de código

**Alternativas evaluadas**:

| Opción | Compatibilidad | Integración |
|--------|---------------|-------------|
| openapi-generator-maven-plugin | Maven | N/A (proyecto Gradle) |
| openapi-generator-gradle-plugin | Gradle ✅ | `build.gradle` |
| openapi-generator CLI manual | Universal | Requiere CI custom |

**Decisión**: **`org.openapi.generator` Gradle plugin** (versión 7.x).

**Rationale**: El proyecto es 100% Gradle. El plugin integra la generación en el ciclo de build
(`generateSources` task), garantizando que los stubs estén siempre sincronizados con el contrato.
El plugin genera interfaces de server stub (`@ApiDelegate`) y DTOs que el adaptador REST implementa.

---

## Decisión 3 — BDD: herramienta de pruebas funcionales

**Alternativas evaluadas**:

| Opción | Sintaxis BDD | Integración JUnit 5 |
|--------|-------------|---------------------|
| Cucumber + JUnit 5 | Gherkin `.feature` | `cucumber-junit-platform-engine` |
| JUnit 5 puro con `@DisplayName` | Java DSL | Nativa |
| JBehave | Gherkin | Configuración propia |

**Decisión**: **Cucumber 7.x + JUnit 5** para pruebas funcionales; **JUnit 5** puro con
`@DisplayName` y Given/When/Then en nombres de método para pruebas unitarias e integración.

**Rationale**: Cucumber permite expresar los escenarios de la spec directamente en archivos
`.feature` (Gherkin), manteniendo trazabilidad directa entre spec → test → código. JUnit 5
es suficiente para unit/integration dado que los escenarios BDD ya están capturados en Cucumber.

---

## Decisión 4 — Cobertura: JaCoCo en Gradle

**Decisión**: Plugin `jacoco` de Gradle + task `jacocoTestReport` + `jacocoTestCoverageVerification`.

**Configuración target**:
```groovy
jacocoTestCoverageVerification {
  violationRules {
    rule { limit { minimum = 0.80 } }            // global ≥ 80%
    rule {
      element = 'CLASS'
      limit { minimum = 0.80; counter = 'LINE' } // por clase > 80%
    }
  }
}
```

Reportes: XML (parsing CI) + HTML (revisión humana), publicados como artefactos de CI.

---

## Decisión 5 — Arquitectura limpia: verificación en tiempo de build

**Decisión**: **ArchUnit** (`com.tngtech.archunit:archunit-junit5`) para validar que no existan
dependencias de capas internas hacia capas externas en tiempo de test.

**Reglas a codificar**:
- `domain` no puede importar nada de `infrastructure` ni de `application`.
- `application` no puede importar nada de `infrastructure`.
- Los controllers (adaptadores entrantes) solo pueden llamar puertos (`port.in`), no use cases directamente.

---

## Decisión 6 — Estado en tiempo real: arquitectura de propagación

Cuando el asesor llama `PUT /api/v1/propiedades/{id}/estado`:
1. El use case `CambiarEstadoPropiedadUseCase` persiste el cambio en JPA.
2. Publica un evento interno (interfaz `PropiedadEstadoCambiado`) inyectado como puerto de salida.
3. El adaptador `SsePropiedadEmitterAdapter` mantiene un registry `Map<String, List<SseEmitter>>`
   y emite el evento a todos los clientes suscritos a esa propiedad.

Este diseño mantiene el use case ignorante de SSE (Principio D de SOLID; Principio I de Clean Arch).

---

## Decisión 7 — Inicialización de base de datos con schema y datos de muestra

**Decisión**: `src/main/resources/db/schema.sql` (DDL) + `src/main/resources/db/data.sql` (DML),
ejecutados automáticamente por Spring Boot SQL Init al arrancar la aplicación.

**Configuración en `application.yaml`**:
```yaml
spring:
  sql:
    init:
      schema-locations: classpath:db/schema.sql
      data-locations: classpath:db/data.sql
      mode: always
  jpa:
    hibernate:
      ddl-auto: none   # Hibernate no toca el schema; lo gestiona schema.sql
```

**Rationale**:
- Separa la responsabilidad de estructura (schema.sql) y datos (data.sql), facilitando
  mantenimiento independiente de cada archivo.
- `ddl-auto: none` garantiza que el schema.sql es la única fuente de verdad del DDL —
  no hay riesgo de que Hibernate regenere o altere tablas inesperadamente.
- Con H2 in-memory, `mode: always` ejecuta los scripts en cada arranque, lo que es
  idóneo para desarrollo y CI (base siempre limpia y predecible).
- Los datos de muestra (`data.sql`) permiten validar manualmente la feature sin scripts
  adicionales de seed — los quickstart scenarios 1–5 funcionan desde el primer arranque.

**Contenido de `data.sql`** (3 propiedades de muestra):
- UUID 1: estado DISPONIBLE
- UUID 2: estado RESERVADA
- UUID 3: estado VENDIDA

Esto cubre los tres estados y permite ejecutar todos los escenarios de `quickstart.md`
sin ningún paso de preparación previo.

---

## Alternativas descartadas (registro)

- **Spring WebFlux**: descartado — el proyecto ya usa Spring Web MVC; migrar introduce riesgo y complejidad fuera del alcance del MVP.
- **Redis Pub/Sub**: descartado — agrega una dependencia de infraestructura innecesaria para un único nodo de despliegue en MVP.
- **Base de datos como bus de eventos** (polling a tabla de eventos): descartado — latencia no determinista y antipatrón de integración.

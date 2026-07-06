<!--
SYNC IMPACT REPORT
==================
Version change: [TEMPLATE] → 1.0.0

Modified principles:
  [PRINCIPLE_1_NAME] → I. Clean Architecture
  [PRINCIPLE_2_NAME] → II. BDD Testing Strategy
  [PRINCIPLE_3_NAME] → III. Good Practices (SOLID, YAGNI, DRY)
  [PRINCIPLE_4_NAME] → IV. API First
  [PRINCIPLE_5_NAME] → V. Coverage Quality Gate

Added sections:
  - Quality Gates & Metrics
  - Development Workflow

Removed sections: none

Templates requiring updates:
  ✅ .specify/templates/plan-template.md — Constitution Check gates reflect 5 principles
  ✅ .specify/templates/spec-template.md — Acceptance Scenarios use BDD (Given/When/Then)
  ✅ .specify/templates/tasks-template.md — Test tasks mandatory; coverage task added to Polish phase

Follow-up TODOs: none — all placeholders resolved.
-->

# CitaSalud Service Constitution

## Core Principles

### I. Clean Architecture

The service MUST be structured following Clean Architecture as defined by Robert C. Martin.
Layers are: Domain (entities, value objects, domain services), Application (use cases, ports),
Infrastructure (adapters: REST, persistence, messaging), and Interface (controllers, DTOs).

- Dependencies MUST point inward only; outer layers depend on inner layers, never the reverse.
- The Domain layer MUST NOT import any framework, library, or infrastructure class.
- Use cases MUST be defined as interfaces (ports) in the Application layer; adapters implement them.
- Every new class MUST be placed in the layer that matches its responsibility; misplaced classes
  are a blocking defect in code review.
- Framework annotations (e.g., Spring `@Service`, `@Repository`) are permitted only in the
  Infrastructure and Interface layers.

### II. BDD Testing Strategy

All features MUST be covered by three test levels using the BDD (Behaviour-Driven Development)
approach. Scenarios MUST be expressed in Given/When/Then format regardless of tool choice.

- **Unit tests**: Cover each class in isolation (Domain and Application layers primarily).
  Collaborators MUST be replaced with test doubles (mocks/stubs). One test class per production class.
- **Integration tests**: Verify that two or more components work together correctly
  (e.g., use-case + repository adapter, controller + use-case). Use a real application context
  with an in-memory or containerized database (TestContainers preferred).
- **Functional / end-to-end tests**: Validate complete user journeys through the exposed API.
  MUST target the running HTTP interface using real requests. Scenarios map 1-to-1 with user stories.
- Tests MUST be written before the implementation (TDD/BDD red-green-refactor cycle).
- A failing test suite MUST block any merge or deployment.

### III. Good Programming Practices (SOLID, YAGNI, DRY)

All code MUST comply with the following principles — violations are blocking review findings:

**SOLID**
- **S** — Single Responsibility: every class has exactly one reason to change.
- **O** — Open/Closed: classes are open for extension, closed for modification; use polymorphism.
- **L** — Liskov Substitution: subtypes MUST be substitutable for their base types without
  altering program correctness.
- **I** — Interface Segregation: interfaces MUST be small and cohesive; no client is forced to
  depend on methods it does not use.
- **D** — Dependency Inversion: high-level modules MUST depend on abstractions, not concretions.

**YAGNI (You Aren't Gonna Need It)**
- Code MUST NOT be written speculatively. A feature is implemented only when there is a concrete,
  accepted requirement for it. Premature abstractions are a defect.

**DRY (Don't Repeat Yourself)**
- Logic or data definition MUST NOT be duplicated. Every piece of knowledge has a single,
  authoritative representation. Duplication discovered in review MUST be refactored before merge.

### IV. API First

All HTTP APIs MUST be designed contract-first using the OpenAPI 3.x specification.

- An OpenAPI contract (`openapi.yml`) MUST exist and be committed to the repository before
  any implementation begins.
- Server stubs and client SDKs MUST be generated from the contract using `openapi-generator-maven-plugin`
  (or the equivalent CLI) — hand-written boilerplate duplicating the contract is forbidden.
- The contract is the single source of truth; any drift between contract and implementation is a
  blocking defect.
- Breaking changes to the contract (removed fields, changed types, removed endpoints) require a
  major version increment and a migration plan documented in the PR description.
- All request/response models in the Interface layer MUST originate from generated DTOs; manual
  DTO classes that duplicate contract models are not permitted.

### V. Coverage Quality Gate

Code coverage MUST be measured by JaCoCo on every build. The following thresholds are non-negotiable
and MUST be enforced as build-breaking rules in the Maven/Gradle configuration:

| Scope | Metric | Minimum |
|-------|--------|---------|
| Per class | Line + branch coverage | > 80 % |
| Global | Overall instruction coverage | ≥ 80 % |

- JaCoCo MUST generate both XML (for CI parsing) and HTML (for human review) reports on every build.
- Reports MUST be published as CI artifacts and linked in every PR.
- Classes intentionally excluded from coverage (e.g., generated DTOs, main bootstrap class) MUST
  be declared explicitly in the JaCoCo exclusion list — blanket exclusions are forbidden.
- A build that falls below any threshold MUST NOT be merged or deployed.

## Quality Gates & Metrics

Before any pull request is merged, ALL of the following gates MUST pass:

1. **Architecture gate**: No inward dependency violations (ArchUnit or equivalent MUST be
   configured and run as part of the test suite).
2. **Contract gate**: `openapi.yml` is present, valid, and the generated code is up to date
   (`mvn generate-sources` produces no diff).
3. **Test gate**: All unit, integration, and functional tests pass (zero failures, zero errors).
4. **Coverage gate**: JaCoCo thresholds pass — per-class > 80 %, global ≥ 80 %.
5. **Code quality gate**: No SOLID, YAGNI, or DRY violations flagged in review (manual gate).

Failing any gate is a blocking condition; the PR MUST NOT be merged until it is resolved.

## Development Workflow

1. **Design the contract first**: Write or update `openapi.yml` before touching any Java class.
2. **Generate**: Run `openapi-generator` to produce stubs and DTOs.
3. **Write failing tests**: Express acceptance scenarios in Given/When/Then; confirm they fail (red).
4. **Implement**: Write the minimum code to make tests pass (green), respecting Clean Architecture
   layer boundaries and SOLID/YAGNI/DRY principles.
5. **Refactor**: Clean up duplication and design issues without breaking tests.
6. **Verify coverage**: Run `mvn verify` (or `./gradlew check`) and confirm JaCoCo thresholds pass.
7. **Open PR**: Attach JaCoCo HTML report link; all gates must be green before requesting review.

## Governance

- This constitution supersedes all previously documented coding standards for `citasalud-service`.
- Amendments require: (1) a documented rationale, (2) team consensus (at least two approvers),
  (3) a migration plan for existing code that would violate the new rule, and
  (4) an update to this file with version increment and `LAST_AMENDED_DATE`.
- Versioning follows Semantic Versioning:
  - **MAJOR**: Removal or redefinition of a principle; backward-incompatible governance change.
  - **MINOR**: New principle or section added; material expansion of existing guidance.
  - **PATCH**: Clarifications, wording fixes, non-semantic refinements.
- Compliance is reviewed at the start of every sprint. Any open violation MUST be tracked as
  a technical-debt ticket and resolved within two sprints of discovery.
- This file is the authoritative governance document; runtime guidance files, templates, and
  CI configuration MUST be kept consistent with it.

**Version**: 1.0.0 | **Ratified**: 2026-06-27 | **Last Amended**: 2026-06-27

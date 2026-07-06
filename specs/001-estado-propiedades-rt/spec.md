# Feature Specification: [US-05] Estado en Tiempo Real de Propiedades

**Feature Branch**: `001-estado-propiedades-rt`

**Created**: 2026-06-27

**Status**: Draft

**Input**: User Story US-05 — Como cliente comprador, quiero ver en el portal si una propiedad
está disponible, reservada o vendida en tiempo real, para no hacer una visita innecesaria a una
propiedad que ya no está libre.

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Actualización automática de estado en el portal (Priority: P1)

Como cliente comprador, cuando un asesor cambia el estado de una propiedad en el sistema interno,
quiero que el portal refleje ese cambio sin que yo tenga que recargar la página, para no perder
tiempo visitando propiedades que ya no están disponibles.

**Why this priority**: Es el núcleo de la funcionalidad MVP — sin sincronización en tiempo real,
el resto de restricciones de estado carecen de valor.

**Independent Test**: El test puede ejecutarse cambiando el estado desde el panel de asesor y
verificando que la ficha del cliente muestra el nuevo estado en menos de 60 segundos, sin
interacción del cliente.

**Acceptance Scenarios**:

1. **Given** un asesor cambia el estado de una propiedad de "disponible" a "reservada" en el
   sistema interno, **When** el cliente visualiza la ficha de esa propiedad en el portal,
   **Then** el estado "reservada" aparece visible en la ficha en menos de 60 segundos.

2. **Given** una propiedad tiene estado "reservada" en el sistema interno, **When** la reserva
   caduca y el asesor revierte el estado a "disponible", **Then** el portal refleja el estado
   "disponible" en menos de 60 segundos.

3. **Given** el sistema interno actualiza el estado de una propiedad, **When** múltiples clientes
   visualizan la misma ficha simultáneamente, **Then** todos reciben el estado actualizado en
   menos de 60 segundos.

---

### User Story 2 — Bloqueo de agendamiento en propiedades reservadas (Priority: P2)

Como cliente comprador, cuando una propiedad está reservada, quiero que el sistema me informe
claramente y no me permita agendar una visita, para no generar expectativas sobre una propiedad
que ya no está disponible.

**Why this priority**: Previene una acción inválida con consecuencias directas en la experiencia
del cliente; depende de la sincronización de estado (US1).

**Independent Test**: Se puede verificar intentando agendar una visita a una propiedad con estado
"reservada" y comprobando que el sistema bloquea la acción y muestra un mensaje informativo.

**Acceptance Scenarios**:

1. **Given** la propiedad está marcada como "reservada", **When** el cliente intenta agendar
   una visita desde la ficha, **Then** el sistema muestra un mensaje informativo que explica
   que la propiedad no está disponible y no permite continuar con el agendamiento.

2. **Given** la propiedad está marcada como "reservada", **When** el cliente visualiza la ficha,
   **Then** el botón o enlace de agendamiento está deshabilitado o ausente, de modo que la
   restricción es visible antes de que el cliente intente hacer clic.

3. **Given** una propiedad reservada vuelve a estado "disponible", **When** el cliente actualiza
   o regresa a la ficha, **Then** la opción de agendamiento vuelve a estar habilitada.

---

### User Story 3 — Visualización de estado "vendida" con restricción de contacto (Priority: P3)

Como cliente comprador, cuando una propiedad está marcada como vendida, quiero verlo claramente
en el portal sin ninguna opción de contacto, para no invertir tiempo en una propiedad que ya
tiene dueño.

**Why this priority**: Complementa el MVP con el estado terminal del ciclo de vida de una
propiedad; depende de la sincronización de estado (US1).

**Independent Test**: Se puede verificar buscando una propiedad con estado "vendida" y confirmando
que aparece con ese estado visible y sin opciones de contacto o agendamiento.

**Acceptance Scenarios**:

1. **Given** una propiedad está marcada como "vendida", **When** el cliente la busca en el portal,
   **Then** la ficha muestra el estado "vendida" de forma claramente visible (ej. etiqueta
   prominente) y no presenta ninguna opción de contacto ni de agendamiento de visita.

2. **Given** una propiedad está marcada como "vendida", **When** el cliente accede directamente
   a la URL de la ficha, **Then** el estado "vendida" es visible en la primera pantalla visible
   sin necesidad de hacer scroll.

3. **Given** el cliente realiza una búsqueda general de propiedades, **When** los resultados
   incluyen propiedades vendidas, **Then** cada propiedad vendida aparece con su estado claramente
   identificado en la tarjeta del listado.

---

### Edge Cases

- ¿Qué ocurre si el sistema interno está temporalmente inaccesible? El portal DEBE mostrar el
  último estado conocido e indicar al cliente que la información podría no estar actualizada.
- ¿Qué ocurre si el estado de una propiedad cambia mientras el cliente está en medio del proceso
  de agendamiento? El sistema DEBE detectar el cambio y abortar el flujo con un mensaje claro
  antes de confirmar la visita.
- ¿Qué ocurre si el cliente tiene la ficha abierta por más de 60 segundos antes de intentar
  agendar? El sistema DEBE verificar el estado actual en el momento de confirmar el agendamiento,
  no solo al cargar la ficha.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE reflejar el cambio de estado de cualquier propiedad en el portal del
  cliente en un máximo de 60 segundos desde que el asesor lo modifica en el sistema interno.
- **FR-002**: El sistema DEBE mostrar el estado actual de la propiedad (disponible, reservada,
  vendida) de forma visualmente prominente en la ficha de la propiedad.
- **FR-003**: El sistema DEBE impedir que un cliente complete el flujo de agendamiento de visita
  si la propiedad tiene estado "reservada" o "vendida".
- **FR-004**: El sistema DEBE mostrar un mensaje informativo al cliente cuando intente agendar
  una visita a una propiedad no disponible, indicando el motivo del bloqueo.
- **FR-005**: El sistema DEBE ocultar o deshabilitar todas las opciones de contacto y agendamiento
  en fichas de propiedades con estado "vendida".
- **FR-006**: El sistema DEBE verificar el estado vigente de la propiedad en el momento exacto en
  que el cliente confirma el agendamiento, independientemente de cuándo cargó la ficha.
- **FR-007**: El sistema DEBE mostrar el estado de la propiedad en las tarjetas del listado de
  búsqueda, no solo en la ficha individual.

### Key Entities

- **Propiedad**: Unidad inmobiliaria con identificador único, datos de presentación (nombre,
  descripción, imágenes, precio) y estado comercial.
- **Estado de Propiedad**: Valor controlado con exactamente tres valores posibles — `disponible`,
  `reservada`, `vendida`. Es el atributo que determina las acciones permitidas para el cliente.
- **Cambio de Estado**: Evento originado por un asesor en el sistema interno que modifica el
  estado de una propiedad y debe propagarse al portal en tiempo real.
- **Ficha de Propiedad**: Vista de detalle que el cliente visualiza en el portal, donde se muestra
  el estado actual y las opciones de interacción disponibles.
- **Agendamiento de Visita**: Flujo que permite a un cliente solicitar una visita presencial a
  una propiedad disponible.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El estado de una propiedad se actualiza en el portal del cliente en menos de
  60 segundos en el 99 % de los casos tras ser modificado en el sistema interno.
- **SC-002**: El 100 % de los intentos de iniciar un agendamiento en propiedades reservadas o
  vendidas son bloqueados antes de que el flujo sea completado.
- **SC-003**: El cliente puede identificar el estado de cualquier propiedad sin ambigüedad en
  los primeros 5 segundos de visualizar la ficha, sin necesidad de interacción adicional.
- **SC-004**: Ninguna propiedad con estado "vendida" presenta opciones de contacto o agendamiento
  accesibles (visibles y activas) para el cliente.
- **SC-005**: Al menos el 90 % de los clientes en pruebas de usabilidad identifican correctamente
  el estado de una propiedad en su primera visualización.

## Assumptions

- Los estados posibles de una propiedad son exactamente tres: **disponible**, **reservada**,
  **vendida**. No existen estados intermedios o personalizados en el alcance de este MVP.
  En código y en el contrato OpenAPI, estos valores se representan como enums en mayúsculas:
  `DISPONIBLE`, `RESERVADA`, `VENDIDA`.
- El sistema interno (panel del asesor) y el portal del cliente son sistemas separados que se
  comunican; el canal de comunicación entre ambos está disponible en condiciones normales.
- El cliente puede visualizar la ficha de una propiedad sin necesidad de autenticarse en el portal.
- Una propiedad con estado "reservada" puede volver a "disponible" si la reserva cae; este caso
  también debe cumplir el límite de 60 segundos de propagación.
- El agendamiento de visita es el único flujo de interacción del cliente que debe bloquearse
  cuando la propiedad no está disponible; otras interacciones (favoritos, compartir) quedan fuera
  del alcance de esta especificación.
- La latencia de red del cliente no está bajo control del sistema; el límite de 60 segundos
  aplica desde que el cambio ocurre en el sistema interno hasta que el dato está disponible
  para ser renderizado en el portal.
- **FR-006 — Límite de responsabilidad del agendamiento**: Este servicio no implementa el flujo
  de confirmación de agendamiento (no existe endpoint POST /agendamiento). La garantía de estado
  vigente se cumple a través del flag `accionesPermitidas.puedeAgendarVisita`, que siempre se
  calcula desde el estado persistido en base de datos en el momento de cada request GET. El portal
  o servicio de agendamiento es responsable de consultar este flag inmediatamente antes de
  confirmar la reserva de visita.
- **US2-AC1 — Mensaje informativo**: El backend entrega `accionesPermitidas.puedeAgendarVisita: false`
  y el campo `estado` como datos suficientes. El portal es responsable de generar y renderizar el
  mensaje informativo al cliente; este servicio no produce texto de UI.

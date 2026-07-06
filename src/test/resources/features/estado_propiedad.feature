# language: es
Característica: Estado en Tiempo Real de Propiedades

  Antecedentes:
    Dado las propiedades están precargadas en el sistema

  Escenario: US1-AC1 - Cambio de estado visible en portal
    Dado una propiedad con id "3fa85f64-5717-4562-b3fc-2c963f66afa6" en estado "DISPONIBLE"
    Cuando el asesor cambia el estado a "RESERVADA"
    Entonces la propiedad muestra el estado "RESERVADA"
    Y el cambio es visible en menos de 60 segundos

  Escenario: US1-AC3 - Listado muestra estado de cada propiedad
    Cuando el cliente consulta el listado de propiedades
    Entonces el listado contiene al menos 3 propiedades
    Y cada propiedad en el listado tiene un campo "estado"

  Escenario: US2-AC1 - Propiedad reservada bloquea agendamiento de visita
    Dado una propiedad con id "b2e8c4a1-1234-5678-9abc-def012345678" en estado "RESERVADA"
    Cuando el cliente consulta la ficha de la propiedad
    Entonces la respuesta indica que no se puede agendar visita

  Escenario: US2-AC2 - Revertir reserva habilita agendamiento
    Dado una propiedad con id "b2e8c4a1-1234-5678-9abc-def012345678" en estado "RESERVADA"
    Cuando el asesor cambia el estado a "DISPONIBLE"
    Entonces la respuesta indica que se puede agendar visita

  Escenario: US3-AC1 - Propiedad vendida oculta todas las opciones de contacto
    Dado una propiedad con id "c9f1d2e3-abcd-ef01-2345-6789abcdef01" en estado "VENDIDA"
    Cuando el cliente consulta la ficha de la propiedad
    Entonces la respuesta indica que no se puede contactar
    Y la respuesta indica que no se puede agendar visita

  Escenario: US3-AC2 - Transición desde estado VENDIDA es rechazada con HTTP 400
    Dado una propiedad con id "c9f1d2e3-abcd-ef01-2345-6789abcdef01" en estado "VENDIDA"
    Cuando el asesor intenta cambiar el estado a "DISPONIBLE"
    Entonces la respuesta tiene código HTTP 400
    Y el código de error es "TRANSICION_ESTADO_INVALIDA"

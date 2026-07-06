package org.ups.citasaludservice.infrastructure.adapter.out.sse;

import org.springframework.stereotype.Component;
import org.ups.citasaludservice.domain.event.PropiedadEstadoCambiado;
import org.ups.citasaludservice.domain.port.out.PropiedadEventPublisherPort;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class SsePropiedadEmitterAdapter implements PropiedadEventPublisherPort {

    private final PropiedadSseEmitterRegistry registry;

    public SsePropiedadEmitterAdapter(PropiedadSseEmitterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void publicar(PropiedadEstadoCambiado evento) {
        var genEstado = org.ups.citasaludservice.generated.model.EstadoPropiedad
            .valueOf(evento.nuevoEstado().name());

        var dto = new org.ups.citasaludservice.generated.model.EstadoUpdateEvent()
            .propiedadId(UUID.fromString(evento.propiedadId()))
            .estado(genEstado)
            .timestamp(evento.timestamp().atOffset(ZoneOffset.UTC));

        registry.notificar(evento.propiedadId(), dto);
    }
}

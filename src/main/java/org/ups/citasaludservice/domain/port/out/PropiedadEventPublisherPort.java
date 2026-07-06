package org.ups.citasaludservice.domain.port.out;

import org.ups.citasaludservice.domain.event.PropiedadEstadoCambiado;

public interface PropiedadEventPublisherPort {
    void publicar(PropiedadEstadoCambiado evento);
}

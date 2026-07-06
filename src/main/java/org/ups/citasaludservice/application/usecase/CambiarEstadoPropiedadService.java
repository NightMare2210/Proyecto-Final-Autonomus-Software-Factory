package org.ups.citasaludservice.application.usecase;

import org.ups.citasaludservice.domain.event.PropiedadEstadoCambiado;
import org.ups.citasaludservice.domain.model.EstadoPropiedad;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.model.PropiedadNoEncontradaException;
import org.ups.citasaludservice.domain.port.in.CambiarEstadoPropiedadUseCase;
import org.ups.citasaludservice.domain.port.out.PropiedadEventPublisherPort;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

public class CambiarEstadoPropiedadService implements CambiarEstadoPropiedadUseCase {

    private final PropiedadRepositoryPort repositoryPort;
    private final PropiedadEventPublisherPort eventPublisherPort;

    public CambiarEstadoPropiedadService(PropiedadRepositoryPort repositoryPort,
                                         PropiedadEventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public Propiedad cambiarEstado(String id, EstadoPropiedad nuevoEstado) {
        Propiedad propiedad = repositoryPort.findById(id)
            .orElseThrow(() -> new PropiedadNoEncontradaException(id));

        propiedad.cambiarEstado(nuevoEstado);
        Propiedad guardada = repositoryPort.save(propiedad);

        eventPublisherPort.publicar(new PropiedadEstadoCambiado(
            guardada.getId(),
            guardada.getEstado(),
            guardada.getUltimaActualizacion()
        ));

        return guardada;
    }
}

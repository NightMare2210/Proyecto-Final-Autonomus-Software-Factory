package org.ups.citasaludservice.application.usecase;

import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.model.PropiedadNoEncontradaException;
import org.ups.citasaludservice.domain.port.in.ObtenerPropiedadUseCase;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

import java.util.List;

public class ObtenerPropiedadService implements ObtenerPropiedadUseCase {

    private final PropiedadRepositoryPort repositoryPort;

    public ObtenerPropiedadService(PropiedadRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Propiedad obtener(String id) {
        return repositoryPort.findById(id)
            .orElseThrow(() -> new PropiedadNoEncontradaException(id));
    }

    @Override
    public List<Propiedad> listar() {
        return repositoryPort.findAll();
    }
}

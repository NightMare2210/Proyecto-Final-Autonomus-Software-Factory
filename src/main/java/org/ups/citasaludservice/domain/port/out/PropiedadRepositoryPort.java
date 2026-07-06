package org.ups.citasaludservice.domain.port.out;

import org.ups.citasaludservice.domain.model.Propiedad;

import java.util.List;
import java.util.Optional;

public interface PropiedadRepositoryPort {
    Optional<Propiedad> findById(String id);
    List<Propiedad> findAll();
    Propiedad save(Propiedad propiedad);
}

package org.ups.citasaludservice.domain.port.in;

import org.ups.citasaludservice.domain.model.Propiedad;

import java.util.List;

public interface ObtenerPropiedadUseCase {
    Propiedad obtener(String id);
    List<Propiedad> listar();
}

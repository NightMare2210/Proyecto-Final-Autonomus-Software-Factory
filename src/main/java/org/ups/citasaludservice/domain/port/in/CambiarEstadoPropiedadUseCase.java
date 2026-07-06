package org.ups.citasaludservice.domain.port.in;

import org.ups.citasaludservice.domain.model.EstadoPropiedad;
import org.ups.citasaludservice.domain.model.Propiedad;

public interface CambiarEstadoPropiedadUseCase {
    Propiedad cambiarEstado(String id, EstadoPropiedad nuevoEstado);
}

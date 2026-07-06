package org.ups.citasaludservice.domain.event;

import org.ups.citasaludservice.domain.model.EstadoPropiedad;

import java.time.Instant;

public record PropiedadEstadoCambiado(
    String propiedadId,
    EstadoPropiedad nuevoEstado,
    Instant timestamp
) {}

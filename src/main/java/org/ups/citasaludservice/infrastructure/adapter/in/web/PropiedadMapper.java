package org.ups.citasaludservice.infrastructure.adapter.in.web;

import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.generated.model.AccionesPermitidas;
import org.ups.citasaludservice.generated.model.EstadoUpdateEvent;
import org.ups.citasaludservice.generated.model.PropiedadResumenResponse;
import org.ups.citasaludservice.generated.model.PropiedadResponse;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class PropiedadMapper {

    private PropiedadMapper() {}

    public static PropiedadResponse toResponse(Propiedad p) {
        var acciones = p.accionesPermitidas();
        var genEstado = org.ups.citasaludservice.generated.model.EstadoPropiedad
            .valueOf(p.getEstado().name());

        return new PropiedadResponse()
            .id(UUID.fromString(p.getId()))
            .nombre(p.getNombre())
            .descripcion(p.getDescripcion())
            .precio(p.getPrecio().doubleValue())
            .estado(genEstado)
            .ultimaActualizacion(p.getUltimaActualizacion().atOffset(ZoneOffset.UTC))
            .accionesPermitidas(new AccionesPermitidas()
                .puedeAgendarVisita(acciones.puedeAgendarVisita())
                .puedeContactar(acciones.puedeContactar()));
    }

    public static PropiedadResumenResponse toResumen(Propiedad p) {
        var acciones = p.accionesPermitidas();
        var genEstado = org.ups.citasaludservice.generated.model.EstadoPropiedad
            .valueOf(p.getEstado().name());

        return new PropiedadResumenResponse()
            .id(UUID.fromString(p.getId()))
            .nombre(p.getNombre())
            .precio(p.getPrecio().doubleValue())
            .estado(genEstado)
            .accionesPermitidas(new AccionesPermitidas()
                .puedeAgendarVisita(acciones.puedeAgendarVisita())
                .puedeContactar(acciones.puedeContactar()));
    }

    public static List<PropiedadResumenResponse> toResumenList(List<Propiedad> propiedades) {
        return propiedades.stream().map(PropiedadMapper::toResumen).toList();
    }

    public static EstadoUpdateEvent toEstadoUpdateEvent(Propiedad p) {
        var genEstado = org.ups.citasaludservice.generated.model.EstadoPropiedad
            .valueOf(p.getEstado().name());
        return new EstadoUpdateEvent()
            .propiedadId(UUID.fromString(p.getId()))
            .estado(genEstado)
            .timestamp(p.getUltimaActualizacion().atOffset(ZoneOffset.UTC));
    }

    public static org.ups.citasaludservice.domain.model.EstadoPropiedad toDomainEstado(
            org.ups.citasaludservice.generated.model.EstadoPropiedad generatedEstado) {
        return org.ups.citasaludservice.domain.model.EstadoPropiedad
            .valueOf(generatedEstado.getValue());
    }
}

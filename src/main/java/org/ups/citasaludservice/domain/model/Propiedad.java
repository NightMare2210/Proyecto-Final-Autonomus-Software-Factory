package org.ups.citasaludservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Propiedad {

    private final String id;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precio;
    private EstadoPropiedad estado;
    private Instant ultimaActualizacion;

    public Propiedad(String id, String nombre, String descripcion, BigDecimal precio,
                     EstadoPropiedad estado, Instant ultimaActualizacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public void cambiarEstado(EstadoPropiedad nuevo) {
        if (!estado.transicionesPermitidas().contains(nuevo)) {
            throw new EstadoTransicionInvalidaException(estado, nuevo);
        }
        this.estado = nuevo;
        this.ultimaActualizacion = Instant.now();
    }

    public AccionesPermitidas accionesPermitidas() {
        return new AccionesPermitidas(
            estado == EstadoPropiedad.DISPONIBLE,
            estado != EstadoPropiedad.VENDIDA
        );
    }

    public String getId()                      { return id; }
    public String getNombre()                  { return nombre; }
    public String getDescripcion()             { return descripcion; }
    public BigDecimal getPrecio()              { return precio; }
    public EstadoPropiedad getEstado()         { return estado; }
    public Instant getUltimaActualizacion()    { return ultimaActualizacion; }

    public record AccionesPermitidas(boolean puedeAgendarVisita, boolean puedeContactar) {}
}

package org.ups.citasaludservice.domain.model;

public class EstadoTransicionInvalidaException extends RuntimeException {

    private final EstadoPropiedad estadoActual;
    private final EstadoPropiedad estadoDestino;

    public EstadoTransicionInvalidaException(EstadoPropiedad estadoActual, EstadoPropiedad estadoDestino) {
        super("No es posible cambiar de " + estadoActual + " a " + estadoDestino);
        this.estadoActual = estadoActual;
        this.estadoDestino = estadoDestino;
    }

    public EstadoPropiedad getEstadoActual() {
        return estadoActual;
    }

    public EstadoPropiedad getEstadoDestino() {
        return estadoDestino;
    }
}

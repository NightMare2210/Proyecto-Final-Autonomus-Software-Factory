package org.ups.citasaludservice.domain.model;

public class PropiedadNoEncontradaException extends RuntimeException {

    private final String propiedadId;

    public PropiedadNoEncontradaException(String propiedadId) {
        super("No se encontró una propiedad con el id: " + propiedadId);
        this.propiedadId = propiedadId;
    }

    public String getPropiedadId() {
        return propiedadId;
    }
}

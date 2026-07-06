package org.ups.citasaludservice.domain.model;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoPropiedad {

    DISPONIBLE {
        @Override
        public Set<EstadoPropiedad> transicionesPermitidas() {
            return EnumSet.of(RESERVADA, VENDIDA);
        }
    },
    RESERVADA {
        @Override
        public Set<EstadoPropiedad> transicionesPermitidas() {
            return EnumSet.of(DISPONIBLE, VENDIDA);
        }
    },
    VENDIDA {
        @Override
        public Set<EstadoPropiedad> transicionesPermitidas() {
            return EnumSet.noneOf(EstadoPropiedad.class);
        }
    };

    public abstract Set<EstadoPropiedad> transicionesPermitidas();
}

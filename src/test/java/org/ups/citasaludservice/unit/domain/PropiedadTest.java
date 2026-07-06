package org.ups.citasaludservice.unit.domain;

import org.junit.jupiter.api.Test;
import org.ups.citasaludservice.domain.model.EstadoPropiedad;
import org.ups.citasaludservice.domain.model.EstadoTransicionInvalidaException;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.model.PropiedadNoEncontradaException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropiedadTest {

    private Propiedad propiedad(EstadoPropiedad estado) {
        return new Propiedad("id-1", "Nombre", "Descripcion", BigDecimal.valueOf(100_000), estado, Instant.now());
    }

    // US1 tests
    @Test
    void creacion_con_estado_inicial_disponible() {
        Propiedad p = propiedad(EstadoPropiedad.DISPONIBLE);
        assertThat(p.getEstado()).isEqualTo(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    void transicion_disponible_a_reservada_valida() {
        Propiedad p = propiedad(EstadoPropiedad.DISPONIBLE);
        p.cambiarEstado(EstadoPropiedad.RESERVADA);
        assertThat(p.getEstado()).isEqualTo(EstadoPropiedad.RESERVADA);
    }

    @Test
    void cambiar_estado_desde_vendida_lanza_excepcion() {
        Propiedad p = propiedad(EstadoPropiedad.VENDIDA);
        assertThatThrownBy(() -> p.cambiarEstado(EstadoPropiedad.DISPONIBLE))
            .isInstanceOf(EstadoTransicionInvalidaException.class);
    }

    @Test
    void acciones_permitidas_para_disponible() {
        Propiedad p = propiedad(EstadoPropiedad.DISPONIBLE);
        var acciones = p.accionesPermitidas();
        assertThat(acciones.puedeAgendarVisita()).isTrue();
        assertThat(acciones.puedeContactar()).isTrue();
    }

    @Test
    void puede_agendar_visita_false_para_reservada() {
        Propiedad p = propiedad(EstadoPropiedad.RESERVADA);
        assertThat(p.accionesPermitidas().puedeAgendarVisita()).isFalse();
    }

    // US2 tests
    @Test
    void acciones_permitidas_para_reservada() {
        Propiedad p = propiedad(EstadoPropiedad.RESERVADA);
        var acciones = p.accionesPermitidas();
        assertThat(acciones.puedeAgendarVisita()).isFalse();
        assertThat(acciones.puedeContactar()).isTrue();
    }

    @Test
    void puede_agendar_visita_true_para_disponible() {
        Propiedad p = propiedad(EstadoPropiedad.DISPONIBLE);
        assertThat(p.accionesPermitidas().puedeAgendarVisita()).isTrue();
    }

    // US3 tests
    @Test
    void ambas_flags_false_para_vendida() {
        Propiedad p = propiedad(EstadoPropiedad.VENDIDA);
        var acciones = p.accionesPermitidas();
        assertThat(acciones.puedeAgendarVisita()).isFalse();
        assertThat(acciones.puedeContactar()).isFalse();
    }

    @Test
    void cambiar_estado_disponible_desde_vendida_lanza_excepcion() {
        Propiedad p = propiedad(EstadoPropiedad.VENDIDA);
        assertThatThrownBy(() -> p.cambiarEstado(EstadoPropiedad.DISPONIBLE))
            .isInstanceOf(EstadoTransicionInvalidaException.class);
    }

    @Test
    void estado_transicion_invalida_exception_expone_estados() {
        var ex = new EstadoTransicionInvalidaException(EstadoPropiedad.VENDIDA, EstadoPropiedad.DISPONIBLE);
        assertThat(ex.getEstadoActual()).isEqualTo(EstadoPropiedad.VENDIDA);
        assertThat(ex.getEstadoDestino()).isEqualTo(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    void propiedad_no_encontrada_exception_expone_id() {
        var ex = new PropiedadNoEncontradaException("test-id-123");
        assertThat(ex.getPropiedadId()).isEqualTo("test-id-123");
    }
}

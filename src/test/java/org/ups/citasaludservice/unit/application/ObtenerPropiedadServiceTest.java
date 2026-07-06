package org.ups.citasaludservice.unit.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasaludservice.application.usecase.ObtenerPropiedadService;
import org.ups.citasaludservice.domain.model.EstadoPropiedad;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.model.PropiedadNoEncontradaException;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerPropiedadServiceTest {

    @Mock private PropiedadRepositoryPort repositoryPort;

    private ObtenerPropiedadService service;

    @BeforeEach
    void setUp() {
        service = new ObtenerPropiedadService(repositoryPort);
    }

    @Test
    void devuelve_propiedad_cuando_existe() {
        Propiedad propiedad = new Propiedad("id-1", "N", "D", BigDecimal.valueOf(100),
            EstadoPropiedad.DISPONIBLE, Instant.now());
        when(repositoryPort.findById("id-1")).thenReturn(Optional.of(propiedad));

        Propiedad resultado = service.obtener("id-1");

        assertThat(resultado.getId()).isEqualTo("id-1");
        assertThat(resultado.getEstado()).isEqualTo(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    void lanza_excepcion_cuando_id_no_existe() {
        when(repositoryPort.findById("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener("no-existe"))
            .isInstanceOf(PropiedadNoEncontradaException.class);
    }
}

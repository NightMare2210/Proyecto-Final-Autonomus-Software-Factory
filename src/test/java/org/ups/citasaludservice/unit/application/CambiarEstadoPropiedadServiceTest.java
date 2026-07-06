package org.ups.citasaludservice.unit.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.citasaludservice.application.usecase.CambiarEstadoPropiedadService;
import org.ups.citasaludservice.domain.event.PropiedadEstadoCambiado;
import org.ups.citasaludservice.domain.model.EstadoPropiedad;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.port.out.PropiedadEventPublisherPort;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CambiarEstadoPropiedadServiceTest {

    @Mock private PropiedadRepositoryPort repositoryPort;
    @Mock private PropiedadEventPublisherPort eventPublisherPort;

    private CambiarEstadoPropiedadService service;

    @BeforeEach
    void setUp() {
        service = new CambiarEstadoPropiedadService(repositoryPort, eventPublisherPort);
    }

    @Test
    void persiste_nuevo_estado_y_publica_evento() {
        Propiedad propiedad = new Propiedad("id-1", "N", "D", BigDecimal.valueOf(100),
            EstadoPropiedad.DISPONIBLE, Instant.now());

        when(repositoryPort.findById("id-1")).thenReturn(Optional.of(propiedad));
        when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Propiedad resultado = service.cambiarEstado("id-1", EstadoPropiedad.RESERVADA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPropiedad.RESERVADA);
        verify(repositoryPort).save(propiedad);

        ArgumentCaptor<PropiedadEstadoCambiado> captor = ArgumentCaptor.forClass(PropiedadEstadoCambiado.class);
        verify(eventPublisherPort).publicar(captor.capture());
        assertThat(captor.getValue().nuevoEstado()).isEqualTo(EstadoPropiedad.RESERVADA);
        assertThat(captor.getValue().propiedadId()).isEqualTo("id-1");
    }
}

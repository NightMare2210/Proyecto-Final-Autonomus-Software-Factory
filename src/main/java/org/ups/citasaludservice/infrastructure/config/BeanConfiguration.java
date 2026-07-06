package org.ups.citasaludservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ups.citasaludservice.application.usecase.CambiarEstadoPropiedadService;
import org.ups.citasaludservice.application.usecase.ObtenerPropiedadService;
import org.ups.citasaludservice.domain.port.in.CambiarEstadoPropiedadUseCase;
import org.ups.citasaludservice.domain.port.in.ObtenerPropiedadUseCase;
import org.ups.citasaludservice.domain.port.out.PropiedadEventPublisherPort;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

@Configuration
public class BeanConfiguration {

    @Bean
    ObtenerPropiedadUseCase obtenerPropiedadUseCase(PropiedadRepositoryPort repositoryPort) {
        return new ObtenerPropiedadService(repositoryPort);
    }

    @Bean
    CambiarEstadoPropiedadUseCase cambiarEstadoPropiedadUseCase(
            PropiedadRepositoryPort repositoryPort,
            PropiedadEventPublisherPort eventPublisherPort) {
        return new CambiarEstadoPropiedadService(repositoryPort, eventPublisherPort);
    }
}

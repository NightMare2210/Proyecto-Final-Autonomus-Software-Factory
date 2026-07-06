package org.ups.citasaludservice.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ups.citasaludservice.domain.model.EstadoTransicionInvalidaException;
import org.ups.citasaludservice.domain.model.PropiedadNoEncontradaException;
import org.ups.citasaludservice.generated.model.ErrorResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EstadoTransicionInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEstadoTransicionInvalida(EstadoTransicionInvalidaException ex) {
        return new ErrorResponse()
            .codigo("TRANSICION_ESTADO_INVALIDA")
            .mensaje(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @ExceptionHandler(PropiedadNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePropiedadNoEncontrada(PropiedadNoEncontradaException ex) {
        return new ErrorResponse()
            .codigo("PROPIEDAD_NO_ENCONTRADA")
            .mensaje(ex.getMessage())
            .timestamp(OffsetDateTime.now(ZoneOffset.UTC));
    }
}

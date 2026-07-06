package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estado comercial de la propiedad. VENDIDA es estado terminal — no permite más transiciones. 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public enum EstadoPropiedad {
  
  DISPONIBLE("DISPONIBLE"),
  
  RESERVADA("RESERVADA"),
  
  VENDIDA("VENDIDA");

  private String value;

  EstadoPropiedad(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static EstadoPropiedad fromValue(String value) {
    for (EstadoPropiedad b : EstadoPropiedad.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


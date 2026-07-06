package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.ups.citasaludservice.generated.model.EstadoPropiedad;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CambiarEstadoRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public class CambiarEstadoRequest {

  private EstadoPropiedad estado;

  public CambiarEstadoRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CambiarEstadoRequest(EstadoPropiedad estado) {
    this.estado = estado;
  }

  public CambiarEstadoRequest estado(EstadoPropiedad estado) {
    this.estado = estado;
    return this;
  }

  /**
   * Get estado
   * @return estado
   */
  @NotNull
  @JsonProperty("estado")
  public EstadoPropiedad getEstado() {
    return estado;
  }

  public void setEstado(EstadoPropiedad estado) {
    this.estado = estado;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CambiarEstadoRequest cambiarEstadoRequest = (CambiarEstadoRequest) o;
    return Objects.equals(this.estado, cambiarEstadoRequest.estado);
  }

  @Override
  public int hashCode() {
    return Objects.hash(estado);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CambiarEstadoRequest {\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


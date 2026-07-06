package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.ups.citasaludservice.generated.model.EstadoPropiedad;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EstadoUpdateEvent
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public class EstadoUpdateEvent {

  private UUID propiedadId;

  private EstadoPropiedad estado;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime timestamp;

  public EstadoUpdateEvent() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EstadoUpdateEvent(UUID propiedadId, EstadoPropiedad estado, OffsetDateTime timestamp) {
    this.propiedadId = propiedadId;
    this.estado = estado;
    this.timestamp = timestamp;
  }

  public EstadoUpdateEvent propiedadId(UUID propiedadId) {
    this.propiedadId = propiedadId;
    return this;
  }

  /**
   * Get propiedadId
   * @return propiedadId
   */
  @NotNull
  @JsonProperty("propiedadId")
  public UUID getPropiedadId() {
    return propiedadId;
  }

  public void setPropiedadId(UUID propiedadId) {
    this.propiedadId = propiedadId;
  }

  public EstadoUpdateEvent estado(EstadoPropiedad estado) {
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

  public EstadoUpdateEvent timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @NotNull
  @JsonProperty("timestamp")
  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EstadoUpdateEvent estadoUpdateEvent = (EstadoUpdateEvent) o;
    return Objects.equals(this.propiedadId, estadoUpdateEvent.propiedadId) &&
        Objects.equals(this.estado, estadoUpdateEvent.estado) &&
        Objects.equals(this.timestamp, estadoUpdateEvent.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propiedadId, estado, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EstadoUpdateEvent {\n");
    sb.append("    propiedadId: ").append(toIndentedString(propiedadId)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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


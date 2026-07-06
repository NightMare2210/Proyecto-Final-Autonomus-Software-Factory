package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import org.ups.citasaludservice.generated.model.AccionesPermitidas;
import org.ups.citasaludservice.generated.model.EstadoPropiedad;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PropiedadResumenResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public class PropiedadResumenResponse {

  private UUID id;

  private String nombre;

  private Double precio;

  private EstadoPropiedad estado;

  private AccionesPermitidas accionesPermitidas;

  public PropiedadResumenResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PropiedadResumenResponse(UUID id, String nombre, Double precio, EstadoPropiedad estado, AccionesPermitidas accionesPermitidas) {
    this.id = id;
    this.nombre = nombre;
    this.precio = precio;
    this.estado = estado;
    this.accionesPermitidas = accionesPermitidas;
  }

  public PropiedadResumenResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PropiedadResumenResponse nombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  /**
   * Get nombre
   * @return nombre
   */
  @NotNull
  @JsonProperty("nombre")
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public PropiedadResumenResponse precio(Double precio) {
    this.precio = precio;
    return this;
  }

  /**
   * Get precio
   * @return precio
   */
  @NotNull
  @JsonProperty("precio")
  public Double getPrecio() {
    return precio;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public PropiedadResumenResponse estado(EstadoPropiedad estado) {
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

  public PropiedadResumenResponse accionesPermitidas(AccionesPermitidas accionesPermitidas) {
    this.accionesPermitidas = accionesPermitidas;
    return this;
  }

  /**
   * Get accionesPermitidas
   * @return accionesPermitidas
   */
  @NotNull
  @JsonProperty("accionesPermitidas")
  public AccionesPermitidas getAccionesPermitidas() {
    return accionesPermitidas;
  }

  public void setAccionesPermitidas(AccionesPermitidas accionesPermitidas) {
    this.accionesPermitidas = accionesPermitidas;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropiedadResumenResponse propiedadResumenResponse = (PropiedadResumenResponse) o;
    return Objects.equals(this.id, propiedadResumenResponse.id) &&
        Objects.equals(this.nombre, propiedadResumenResponse.nombre) &&
        Objects.equals(this.precio, propiedadResumenResponse.precio) &&
        Objects.equals(this.estado, propiedadResumenResponse.estado) &&
        Objects.equals(this.accionesPermitidas, propiedadResumenResponse.accionesPermitidas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nombre, precio, estado, accionesPermitidas);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropiedadResumenResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    precio: ").append(toIndentedString(precio)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    accionesPermitidas: ").append(toIndentedString(accionesPermitidas)).append("\n");
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


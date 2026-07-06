package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.ups.citasaludservice.generated.model.AccionesPermitidas;
import org.ups.citasaludservice.generated.model.EstadoPropiedad;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PropiedadResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public class PropiedadResponse {

  private UUID id;

  private String nombre;

  private String descripcion;

  private Double precio;

  private EstadoPropiedad estado;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime ultimaActualizacion;

  private AccionesPermitidas accionesPermitidas;

  public PropiedadResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PropiedadResponse(UUID id, String nombre, String descripcion, Double precio, EstadoPropiedad estado, OffsetDateTime ultimaActualizacion) {
    this.id = id;
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.precio = precio;
    this.estado = estado;
    this.ultimaActualizacion = ultimaActualizacion;
  }

  public PropiedadResponse id(UUID id) {
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

  public PropiedadResponse nombre(String nombre) {
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

  public PropiedadResponse descripcion(String descripcion) {
    this.descripcion = descripcion;
    return this;
  }

  /**
   * Get descripcion
   * @return descripcion
   */
  @NotNull
  @JsonProperty("descripcion")
  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public PropiedadResponse precio(Double precio) {
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

  public PropiedadResponse estado(EstadoPropiedad estado) {
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

  public PropiedadResponse ultimaActualizacion(OffsetDateTime ultimaActualizacion) {
    this.ultimaActualizacion = ultimaActualizacion;
    return this;
  }

  /**
   * Get ultimaActualizacion
   * @return ultimaActualizacion
   */
  @NotNull
  @JsonProperty("ultimaActualizacion")
  public OffsetDateTime getUltimaActualizacion() {
    return ultimaActualizacion;
  }

  public void setUltimaActualizacion(OffsetDateTime ultimaActualizacion) {
    this.ultimaActualizacion = ultimaActualizacion;
  }

  public PropiedadResponse accionesPermitidas(AccionesPermitidas accionesPermitidas) {
    this.accionesPermitidas = accionesPermitidas;
    return this;
  }

  /**
   * Get accionesPermitidas
   * @return accionesPermitidas
   */
  
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
    PropiedadResponse propiedadResponse = (PropiedadResponse) o;
    return Objects.equals(this.id, propiedadResponse.id) &&
        Objects.equals(this.nombre, propiedadResponse.nombre) &&
        Objects.equals(this.descripcion, propiedadResponse.descripcion) &&
        Objects.equals(this.precio, propiedadResponse.precio) &&
        Objects.equals(this.estado, propiedadResponse.estado) &&
        Objects.equals(this.ultimaActualizacion, propiedadResponse.ultimaActualizacion) &&
        Objects.equals(this.accionesPermitidas, propiedadResponse.accionesPermitidas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nombre, descripcion, precio, estado, ultimaActualizacion, accionesPermitidas);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropiedadResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    nombre: ").append(toIndentedString(nombre)).append("\n");
    sb.append("    descripcion: ").append(toIndentedString(descripcion)).append("\n");
    sb.append("    precio: ").append(toIndentedString(precio)).append("\n");
    sb.append("    estado: ").append(toIndentedString(estado)).append("\n");
    sb.append("    ultimaActualizacion: ").append(toIndentedString(ultimaActualizacion)).append("\n");
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


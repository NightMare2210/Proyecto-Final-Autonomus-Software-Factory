package org.ups.citasaludservice.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AccionesPermitidas
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.10.0")
public class AccionesPermitidas {

  private Boolean puedeAgendarVisita;

  private Boolean puedeContactar;

  public AccionesPermitidas() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AccionesPermitidas(Boolean puedeAgendarVisita, Boolean puedeContactar) {
    this.puedeAgendarVisita = puedeAgendarVisita;
    this.puedeContactar = puedeContactar;
  }

  public AccionesPermitidas puedeAgendarVisita(Boolean puedeAgendarVisita) {
    this.puedeAgendarVisita = puedeAgendarVisita;
    return this;
  }

  /**
   * true solo si estado es DISPONIBLE
   * @return puedeAgendarVisita
   */
  @NotNull
  @JsonProperty("puedeAgendarVisita")
  public Boolean getPuedeAgendarVisita() {
    return puedeAgendarVisita;
  }

  public void setPuedeAgendarVisita(Boolean puedeAgendarVisita) {
    this.puedeAgendarVisita = puedeAgendarVisita;
  }

  public AccionesPermitidas puedeContactar(Boolean puedeContactar) {
    this.puedeContactar = puedeContactar;
    return this;
  }

  /**
   * true solo si estado no es VENDIDA
   * @return puedeContactar
   */
  @NotNull
  @JsonProperty("puedeContactar")
  public Boolean getPuedeContactar() {
    return puedeContactar;
  }

  public void setPuedeContactar(Boolean puedeContactar) {
    this.puedeContactar = puedeContactar;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccionesPermitidas accionesPermitidas = (AccionesPermitidas) o;
    return Objects.equals(this.puedeAgendarVisita, accionesPermitidas.puedeAgendarVisita) &&
        Objects.equals(this.puedeContactar, accionesPermitidas.puedeContactar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(puedeAgendarVisita, puedeContactar);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccionesPermitidas {\n");
    sb.append("    puedeAgendarVisita: ").append(toIndentedString(puedeAgendarVisita)).append("\n");
    sb.append("    puedeContactar: ").append(toIndentedString(puedeContactar)).append("\n");
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


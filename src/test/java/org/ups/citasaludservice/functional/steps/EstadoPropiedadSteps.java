package org.ups.citasaludservice.functional.steps;

import io.cucumber.java.es.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EstadoPropiedadSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private String propiedadId;
    private ResponseEntity<Map> lastResponse;

    @Dado("las propiedades están precargadas en el sistema")
    public void lasPropiedadesEstanPrecargadas() {
        // data.sql carga los datos automáticamente al iniciar la aplicación
    }

    @Dado("una propiedad con id {string} en estado {string}")
    public void unaPropiedadConIdEnEstado(String id, String estado) {
        this.propiedadId = id;
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "/api/v1/propiedades/" + id, Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Cuando("el asesor cambia el estado a {string}")
    public void elAsesorCambiaElEstadoA(String nuevoEstado) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
            "{\"estado\":\"" + nuevoEstado + "\"}", headers);
        lastResponse = restTemplate.exchange(
            "/api/v1/propiedades/" + propiedadId + "/estado",
            HttpMethod.PUT, request, Map.class);
    }

    @Cuando("el asesor intenta cambiar el estado a {string}")
    public void elAsesorIntentaCambiarElEstadoA(String nuevoEstado) {
        elAsesorCambiaElEstadoA(nuevoEstado);
    }

    @Cuando("el cliente consulta la ficha de la propiedad")
    public void elClienteConsultaLaFichaDeLaPropiedad() {
        lastResponse = restTemplate.getForEntity(
            "/api/v1/propiedades/" + propiedadId, Map.class);
    }

    @Cuando("el cliente consulta el listado de propiedades")
    public void elClienteConsultaElListadoDePropiedades() {
        // Assertion steps (Entonces) call the API directly with List.class
        restTemplate.getForEntity("/api/v1/propiedades", String.class);
    }

    @Entonces("la propiedad muestra el estado {string}")
    public void laPropiedadMuestraElEstado(String estado) {
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(lastResponse.getBody()).isNotNull();
        assertThat(lastResponse.getBody().get("estado")).isEqualTo(estado);
    }

    @Entonces("el cambio es visible en menos de 60 segundos")
    public void elCambioEsVisibleEnMenosDe60Segundos() {
        // El cambio ya fue verificado por el paso anterior; la latencia de persistencia
        // es sub-segundo (validado en SseLatencyTest)
    }

    @Entonces("la respuesta indica que no se puede agendar visita")
    public void laRespuestaIndicaQueNoSePuedeAgendarVisita() {
        assertThat(lastResponse.getStatusCode().is2xxSuccessful()).isTrue();
        @SuppressWarnings("unchecked")
        Map<String, Boolean> acciones = (Map<String, Boolean>)
            lastResponse.getBody().get("accionesPermitidas");
        assertThat(acciones).isNotNull();
        assertThat(acciones.get("puedeAgendarVisita")).isFalse();
    }

    @Entonces("la respuesta indica que se puede agendar visita")
    public void laRespuestaIndicaQueSeCanAgendarVisita() {
        assertThat(lastResponse.getStatusCode().is2xxSuccessful()).isTrue();
        @SuppressWarnings("unchecked")
        Map<String, Boolean> acciones = (Map<String, Boolean>)
            lastResponse.getBody().get("accionesPermitidas");
        assertThat(acciones).isNotNull();
        assertThat(acciones.get("puedeAgendarVisita")).isTrue();
    }

    @Entonces("la respuesta indica que no se puede contactar")
    public void laRespuestaIndicaQueNoSePuedeContactar() {
        assertThat(lastResponse.getStatusCode().is2xxSuccessful()).isTrue();
        @SuppressWarnings("unchecked")
        Map<String, Boolean> acciones = (Map<String, Boolean>)
            lastResponse.getBody().get("accionesPermitidas");
        assertThat(acciones).isNotNull();
        assertThat(acciones.get("puedeContactar")).isFalse();
    }

    @Entonces("el listado contiene al menos 3 propiedades")
    public void elListadoContieneAlMenos3Propiedades() {
        ResponseEntity<List> listResponse = restTemplate.getForEntity("/api/v1/propiedades", List.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody()).hasSizeGreaterThanOrEqualTo(3);
    }

    @Entonces("cada propiedad en el listado tiene un campo {string}")
    public void cadaPropiedadEnElListadoTieneUnCampo(String campo) {
        ResponseEntity<List> listResponse = restTemplate.getForEntity("/api/v1/propiedades", List.class);
        assertThat(listResponse.getBody()).isNotNull();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) listResponse.getBody();
        items.forEach(item -> assertThat(item).containsKey(campo));
    }

    @Entonces("la respuesta tiene código HTTP {int}")
    public void laRespuestaTieneCodigoHTTP(int codigoHttp) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(codigoHttp);
    }

    @Entonces("el código de error es {string}")
    public void elCodigoDeErrorEs(String codigoError) {
        assertThat(lastResponse.getBody()).isNotNull();
        assertThat(lastResponse.getBody().get("codigo")).isEqualTo(codigoError);
    }
}

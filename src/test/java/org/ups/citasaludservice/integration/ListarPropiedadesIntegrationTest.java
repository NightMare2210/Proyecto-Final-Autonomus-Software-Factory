package org.ups.citasaludservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ListarPropiedadesIntegrationTest {

    static final String VENDIDA_ID = "c9f1d2e3-abcd-ef01-2345-6789abcdef01";

    @Autowired MockMvc mockMvc;

    @Test
    void get_propiedades_retorna_array_de_3_elementos() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void cada_elemento_del_listado_tiene_campo_estado() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].estado", everyItem(notNullValue())));
    }

    @Test
    void propiedad_vendida_tiene_puedeContactar_false_en_listado() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$[?(@.id=='" + VENDIDA_ID + "')].accionesPermitidas.puedeContactar",
                contains(false)));
    }
}

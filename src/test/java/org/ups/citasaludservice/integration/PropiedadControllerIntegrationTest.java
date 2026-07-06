package org.ups.citasaludservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PropiedadControllerIntegrationTest {

    static final String DISPONIBLE_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
    static final String RESERVADA_ID  = "b2e8c4a1-1234-5678-9abc-def012345678";
    static final String VENDIDA_ID    = "c9f1d2e3-abcd-ef01-2345-6789abcdef01";
    static final String UNKNOWN_ID    = "00000000-0000-0000-0000-000000000000";

    @Autowired MockMvc mockMvc;

    @Test
    void get_listado_retorna_200_con_3_items() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void get_propiedad_disponible_retorna_200() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades/{id}", DISPONIBLE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
            .andExpect(jsonPath("$.accionesPermitidas.puedeAgendarVisita").value(true))
            .andExpect(jsonPath("$.accionesPermitidas.puedeContactar").value(true));
    }

    @Test
    void get_propiedad_id_inexistente_retorna_404() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades/{id}", UNKNOWN_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.codigo").value("PROPIEDAD_NO_ENCONTRADA"));
    }

    @Test
    void put_estado_vendida_a_disponible_retorna_400() throws Exception {
        mockMvc.perform(put("/api/v1/propiedades/{id}/estado", VENDIDA_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"DISPONIBLE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("TRANSICION_ESTADO_INVALIDA"));
    }

    @Test
    void fresh_state_tras_put_estado() throws Exception {
        // Cambiar DISPONIBLE → RESERVADA
        mockMvc.perform(put("/api/v1/propiedades/{id}/estado", DISPONIBLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"RESERVADA\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("RESERVADA"));

        // Verificar que GET refleja el nuevo estado inmediatamente (sin caché)
        mockMvc.perform(get("/api/v1/propiedades/{id}", DISPONIBLE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("RESERVADA"));

        // Restaurar a DISPONIBLE para no afectar otros tests
        mockMvc.perform(put("/api/v1/propiedades/{id}/estado", DISPONIBLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"DISPONIBLE\"}"))
            .andExpect(status().isOk());
    }
}

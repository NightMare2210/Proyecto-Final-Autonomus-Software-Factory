package org.ups.citasaludservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SseLatencyTest {

    static final String DISPONIBLE_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

    @Autowired MockMvc mockMvc;

    @Test
    void sse_endpoint_activo_responde_con_text_event_stream() throws Exception {
        mockMvc.perform(get("/api/v1/propiedades/{id}/estado/stream", DISPONIBLE_ID)
                .accept(MediaType.TEXT_EVENT_STREAM))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM));
    }

    @Test
    void put_estado_seguido_de_get_refleja_cambio_en_menos_de_2_segundos() throws Exception {
        long inicio = System.currentTimeMillis();

        // DISPONIBLE → RESERVADA
        mockMvc.perform(put("/api/v1/propiedades/{id}/estado", DISPONIBLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"RESERVADA\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("RESERVADA"));

        // GET inmediatamente después refleja el nuevo estado (SC-001)
        mockMvc.perform(get("/api/v1/propiedades/{id}", DISPONIBLE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("RESERVADA"));

        long latencia = System.currentTimeMillis() - inicio;
        assertThat(latencia).isLessThan(2000L);
    }
}

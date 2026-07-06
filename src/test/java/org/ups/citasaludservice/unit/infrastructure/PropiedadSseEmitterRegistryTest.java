package org.ups.citasaludservice.unit.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ups.citasaludservice.infrastructure.adapter.out.sse.PropiedadSseEmitterRegistry;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PropiedadSseEmitterRegistryTest {

    private final PropiedadSseEmitterRegistry registry = new PropiedadSseEmitterRegistry();

    @Test
    void agregar_sin_initial_event_no_llama_send() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        registry.agregar("id-1", emitter, null);
        verify(emitter, never()).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void agregar_con_initial_event_llama_send() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        SseEmitter.SseEventBuilder event = SseEmitter.event().data("test");
        registry.agregar("id-1", emitter, event);
        verify(emitter).send(event);
    }

    @Test
    void agregar_cuando_send_lanza_ioexception_completa_con_error() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        SseEmitter.SseEventBuilder event = SseEmitter.event().data("test");
        IOException cause = new IOException("broken pipe");
        doThrow(cause).when(emitter).send(event);

        registry.agregar("id-1", emitter, event);

        verify(emitter).completeWithError(cause);
    }

    @Test
    void notificar_sin_emitters_registrados_no_lanza_excepcion() {
        registry.notificar("no-existe", "datos");
    }

    @Test
    void notificar_con_emitter_muerto_lo_elimina() throws IOException {
        SseEmitter dead = mock(SseEmitter.class);
        doThrow(new IOException("closed")).when(dead).send(any(SseEmitter.SseEventBuilder.class));
        registry.agregar("id-2", dead, null);

        registry.notificar("id-2", "datos");
    }

    @Test
    void remover_emitter_existente() throws IOException {
        SseEmitter emitter = mock(SseEmitter.class);
        registry.agregar("id-3", emitter, null);
        registry.remover("id-3", emitter);
        // After removal, notificar should not call send on the removed emitter
        registry.notificar("id-3", "datos");
        verify(emitter, never()).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void remover_id_inexistente_no_lanza_excepcion() {
        SseEmitter emitter = mock(SseEmitter.class);
        registry.remover("no-existe", emitter);
    }
}

package org.ups.citasaludservice.infrastructure.adapter.out.sse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class PropiedadSseEmitterRegistry {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters =
        new ConcurrentHashMap<>();

    public void agregar(String propiedadId, SseEmitter emitter, SseEmitter.SseEventBuilder initialEvent) {
        emitters.computeIfAbsent(propiedadId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        if (initialEvent != null) {
            try {
                emitter.send(initialEvent);
            } catch (IOException e) {
                emitter.completeWithError(e);
                remover(propiedadId, emitter);
            }
        }
    }

    public void notificar(String propiedadId, Object data) {
        CopyOnWriteArrayList<SseEmitter> propEmitters =
            emitters.getOrDefault(propiedadId, new CopyOnWriteArrayList<>());
        List<SseEmitter> dead = new ArrayList<>();

        for (SseEmitter emitter : propEmitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("estado-cambiado")
                    .data(data, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                dead.add(emitter);
            }
        }
        propEmitters.removeAll(dead);
    }

    public void remover(String propiedadId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> propEmitters = emitters.get(propiedadId);
        if (propEmitters != null) {
            propEmitters.remove(emitter);
        }
    }
}

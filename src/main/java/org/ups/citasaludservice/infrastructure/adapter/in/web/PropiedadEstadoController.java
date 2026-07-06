package org.ups.citasaludservice.infrastructure.adapter.in.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.port.in.CambiarEstadoPropiedadUseCase;
import org.ups.citasaludservice.domain.port.in.ObtenerPropiedadUseCase;
import org.ups.citasaludservice.generated.model.CambiarEstadoRequest;
import org.ups.citasaludservice.generated.model.PropiedadResponse;
import org.ups.citasaludservice.infrastructure.adapter.out.sse.PropiedadSseEmitterRegistry;

@RestController
@RequestMapping("/api/v1/propiedades")
public class PropiedadEstadoController {

    private final CambiarEstadoPropiedadUseCase cambiarEstadoUseCase;
    private final ObtenerPropiedadUseCase obtenerPropiedadUseCase;
    private final PropiedadSseEmitterRegistry registry;

    public PropiedadEstadoController(CambiarEstadoPropiedadUseCase cambiarEstadoUseCase,
                                     ObtenerPropiedadUseCase obtenerPropiedadUseCase,
                                     PropiedadSseEmitterRegistry registry) {
        this.cambiarEstadoUseCase = cambiarEstadoUseCase;
        this.obtenerPropiedadUseCase = obtenerPropiedadUseCase;
        this.registry = registry;
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<PropiedadResponse> cambiarEstado(
            @PathVariable String id,
            @RequestBody CambiarEstadoRequest request) {
        var domainEstado = PropiedadMapper.toDomainEstado(request.getEstado());
        Propiedad propiedad = cambiarEstadoUseCase.cambiarEstado(id, domainEstado);
        return ResponseEntity.ok(PropiedadMapper.toResponse(propiedad));
    }

    @GetMapping(value = "/{id}/estado/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEstado(@PathVariable String id) {
        Propiedad propiedad = obtenerPropiedadUseCase.obtener(id);
        SseEmitter emitter = new SseEmitter(-1L);

        var initialEvent = SseEmitter.event()
            .name("estado-cambiado")
            .data(PropiedadMapper.toEstadoUpdateEvent(propiedad), MediaType.APPLICATION_JSON);

        registry.agregar(id, emitter, initialEvent);

        emitter.onCompletion(() -> registry.remover(id, emitter));
        emitter.onTimeout(() -> registry.remover(id, emitter));
        emitter.onError(ex -> registry.remover(id, emitter));

        return emitter;
    }
}

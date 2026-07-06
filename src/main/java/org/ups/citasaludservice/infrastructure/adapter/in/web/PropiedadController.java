package org.ups.citasaludservice.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.port.in.ObtenerPropiedadUseCase;
import org.ups.citasaludservice.generated.model.PropiedadResumenResponse;
import org.ups.citasaludservice.generated.model.PropiedadResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/propiedades")
public class PropiedadController {

    private final ObtenerPropiedadUseCase obtenerPropiedadUseCase;

    public PropiedadController(ObtenerPropiedadUseCase obtenerPropiedadUseCase) {
        this.obtenerPropiedadUseCase = obtenerPropiedadUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PropiedadResumenResponse>> listar() {
        List<Propiedad> propiedades = obtenerPropiedadUseCase.listar();
        return ResponseEntity.ok(PropiedadMapper.toResumenList(propiedades));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropiedadResponse> obtener(@PathVariable String id) {
        Propiedad propiedad = obtenerPropiedadUseCase.obtener(id);
        return ResponseEntity.ok(PropiedadMapper.toResponse(propiedad));
    }
}

package org.ups.citasaludservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import org.ups.citasaludservice.domain.model.Propiedad;
import org.ups.citasaludservice.domain.port.out.PropiedadRepositoryPort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
public class PropiedadRepositoryAdapter implements PropiedadRepositoryPort {

    private final PropiedadJpaRepository jpaRepository;

    public PropiedadRepositoryAdapter(PropiedadJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Propiedad> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Propiedad> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Propiedad save(Propiedad propiedad) {
        return toDomain(jpaRepository.save(toJpa(propiedad)));
    }

    private Propiedad toDomain(PropiedadJpaEntity entity) {
        return new Propiedad(
            entity.getId(),
            entity.getNombre(),
            entity.getDescripcion(),
            entity.getPrecio(),
            entity.getEstado(),
            entity.getUltimaActualizacion().toInstant(ZoneOffset.UTC)
        );
    }

    private PropiedadJpaEntity toJpa(Propiedad propiedad) {
        PropiedadJpaEntity entity = new PropiedadJpaEntity();
        entity.setId(propiedad.getId());
        entity.setNombre(propiedad.getNombre());
        entity.setDescripcion(propiedad.getDescripcion());
        entity.setPrecio(propiedad.getPrecio());
        entity.setEstado(propiedad.getEstado());
        entity.setUltimaActualizacion(
            LocalDateTime.ofInstant(propiedad.getUltimaActualizacion(), ZoneOffset.UTC)
        );
        return entity;
    }
}

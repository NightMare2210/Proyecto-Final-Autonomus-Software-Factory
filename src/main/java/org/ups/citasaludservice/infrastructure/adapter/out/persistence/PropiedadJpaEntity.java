package org.ups.citasaludservice.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ups.citasaludservice.domain.model.EstadoPropiedad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "propiedad")
@Getter
@Setter
@NoArgsConstructor
public class PropiedadJpaEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPropiedad estado;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;
}

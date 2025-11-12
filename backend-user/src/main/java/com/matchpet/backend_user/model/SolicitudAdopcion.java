package com.matchpet.backend_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchpet.backend_user.model.lookup.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Solicitudes_Adopcion")
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitud_id")
    private Integer id;

    @CreationTimestamp
    @Column(name = "fecha_solicitud", updatable = false)
    private Timestamp fechaSolicitud;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas; // Notas del refugio (privadas)

    @Column(name = "mensaje_al_adoptante", columnDefinition = "TEXT")
    private String mensajeAlAdoptante; // Mensaje del refugio al adoptante

    // --- Relaciones ---

    /**
     * La solicitud es enviada por UN usuario (Adoptante)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel adoptante;

    /**
     * La solicitud es para UN animal
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Animal animal;

    /**
     * La solicitud tiene UN estado (Enviada, Aprobada, etc.)
     */
    @ManyToOne(fetch = FetchType.EAGER) // Eager para mostrarlo siempre
    @JoinColumn(name = "estado_solicitud_id", nullable = false)
    private EstadoSolicitud estadoSolicitud;
}
package com.matchpet.backend_user.model.lookup; // o .model.lookup

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Estados_Solicitud")
public class EstadoSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_solicitud_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
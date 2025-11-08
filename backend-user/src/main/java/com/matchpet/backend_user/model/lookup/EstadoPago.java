package com.matchpet.backend_user.model.lookup; // o .model.lookup

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Estados_Pago")
public class EstadoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_pago_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
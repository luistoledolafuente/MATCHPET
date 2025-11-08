package com.matchpet.backend_user.model.lookup; // o .model.lookup

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Niveles_Energia")
public class NivelEnergia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nivel_energia_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
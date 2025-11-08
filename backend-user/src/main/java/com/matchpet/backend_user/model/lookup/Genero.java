package com.matchpet.backend_user.model.lookup;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Generos")
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genero_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
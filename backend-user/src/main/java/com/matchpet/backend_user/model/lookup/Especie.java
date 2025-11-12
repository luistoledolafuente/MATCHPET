package com.matchpet.backend_user.model.lookup;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Especies")
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "especie_id")
    private Integer id;

    @Column(name = "nombre_especie", nullable = false, unique = true, length = 50)
    private String nombreEspecie;
}
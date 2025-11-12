package com.matchpet.backend_user.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Temperamentos")
public class Temperamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperamento_id")
    private Integer id;

    @Column(name = "nombre_temperamento", nullable = false, unique = true, length = 100)
    private String nombreTemperamento;
}
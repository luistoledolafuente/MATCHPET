package com.matchpet.backend_user.model.lookup; // o .model.lookup

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Tamanos")
public class Tamano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tamano_id")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
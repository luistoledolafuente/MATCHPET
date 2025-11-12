package com.matchpet.backend_user.model;

import com.matchpet.backend_user.model.lookup.Especie;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Razas")
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raza_id")
    private Integer id;

    @Column(name = "nombre_raza", nullable = false, unique = true, length = 100)
    private String nombreRaza;

    // --- ¡CORRECCIÓN AQUÍ! ---
    // Cambiado de LAZY a EAGER.
    // Siempre queremos saber la especie cuando cargamos una raza.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;
}
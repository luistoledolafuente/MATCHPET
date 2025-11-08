package com.matchpet.backend_user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Refugios")
public class Refugio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refugio_id")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "persona_contacto")
    private String personaContacto;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private Timestamp fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    /**
     * Un Refugio está "administrado" por un UserModel.
     * 'mappedBy = "refugio"' le dice a JPA que la relación
     * (la columna FK) se define en el campo 'refugio' de la clase UserModel.
     */
    @OneToOne(mappedBy = "refugio")
    private UserModel user;
}
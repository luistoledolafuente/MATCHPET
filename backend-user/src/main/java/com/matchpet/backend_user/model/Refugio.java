package com.matchpet.backend_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
// ¡Se quitaron las importaciones de Animal y Set!

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
     */
    @OneToOne(mappedBy = "refugio")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    // --- ¡SE BORRÓ EL CAMPO 'animales' DE AQUÍ! ---
    // (Lo volveremos a añadir DESPUÉS de crear la clase Animal)
}
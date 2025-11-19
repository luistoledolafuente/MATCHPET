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
import java.util.Set; // Import requerido para la colección de Animales

@Data
@NoArgsConstructor
@Entity
@Table(name = "Refugios")
public class Refugio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refugio_id")
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 100)
    private String pais;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "persona_contacto", nullable = false, length = 255)
    private String personaContacto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_sitio_web", nullable = false, length = 255)
    private String urlSitioWeb;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false, nullable = false)
    private Timestamp fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private Timestamp fechaActualizacion;

    /**
     * Relación con el usuario que administra el refugio.
     * 'mappedBy="refugio"' asume que la clase UserModel tiene el campo 'private Refugio refugio;'
     * que posee la relación (con @JoinColumn).
     */
    @OneToOne(mappedBy = "refugio")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    /**
     * Relación con los animales que pertenecen a este refugio.
     */
    @OneToMany(mappedBy = "refugio")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Animal> animales;
}
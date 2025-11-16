package com.matchpet.backend_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matchpet.backend_user.model.lookup.EstadoAdopcion;
import com.matchpet.backend_user.model.lookup.Genero;
import com.matchpet.backend_user.model.lookup.NivelEnergia;
import com.matchpet.backend_user.model.lookup.Tamano;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Animales")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Integer animal_id; // <-- ARREGLO 1: Renombrado

    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(name = "fecha_nacimiento_aprox")
    private Date fechaNacimientoAprox;
    @Column(name = "descripcion_personalidad", columnDefinition = "TEXT")
    private String descripcionPersonalidad;
    @Column(name = "compatible_niños")
    private Boolean compatibleNiños = false; // (Este campo está bien)
    @Column(name = "compatible_otras_mascotas")
    private Boolean compatibleOtrasMascotas = false;
    @Column(name = "esta_vacunado")
    private Boolean estaVacunado = false;
    @Column(name = "esta_esterilizado")
    private Boolean estaEsterilizado = false;
    @Column(name = "historial_medico", columnDefinition = "TEXT")
    private String historialMedico;
    @Column(name = "fecha_ingreso_refugio")
    private Date fechaIngresoRefugio;
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private Timestamp fechaActualizacion;

    // --- Relaciones (Foreign Keys) ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raza_id", nullable = false)
    private Raza raza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refugio_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Refugio refugio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tamano_id")
    private Tamano tamano;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nivel_energia_id")
    private NivelEnergia nivelEnergia;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_adopcion_id", nullable = false)
    private EstadoAdopcion estadoAdopcion;

    // --- Relaciones (Tablas Externas) ---
    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnimalFoto> fotos = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Animal_Temperamentos",
            joinColumns = @JoinColumn(name = "animal_id"),
            inverseJoinColumns = @JoinColumn(name = "temperamento_id")
    )
    private Set<Temperamento> temperamentos = new HashSet<>();


}
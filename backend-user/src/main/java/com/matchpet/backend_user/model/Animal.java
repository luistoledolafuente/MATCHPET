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
import lombok.EqualsAndHashCode;

import java.time.LocalDate; // CAMBIO: Importación moderna para fechas
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Animales")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    @EqualsAndHashCode.Include
    private Integer id; // Práctica común: usar 'id' en Java

    @Column(nullable = false, length = 100)
    private String nombre;

    // CAMBIO: Tipo cambiado a LocalDate y hecho NOT NULL
    @Column(name = "fecha_nacimiento_aprox", nullable = false)
    private LocalDate fechaNacimientoAprox;

    // CAMBIO: Hecho NOT NULL
    @Column(name = "descripcion_personalidad", columnDefinition = "TEXT", nullable = false)
    private String descripcionPersonalidad;

    // CAMBIO: Hecho NOT NULL (primitive 'boolean' es 'false' por defecto)
    @Column(name = "compatible_niños", nullable = false)
    private boolean compatibleNiños = false;

    // CAMBIO: Hecho NOT NULL
    @Column(name = "compatible_otras_mascotas", nullable = false)
    private boolean compatibleOtrasMascotas = false;

    // CAMBIO: Hecho NOT NULL
    @Column(name = "esta_vacunado", nullable = false)
    private boolean estaVacunado = false;

    // CAMBIO: Hecho NOT NULL
    @Column(name = "esta_esterilizado", nullable = false)
    private boolean estaEsterilizado = false;

    // CAMBIO: Hecho NOT NULL
    @Column(name = "historial_medico", columnDefinition = "TEXT", nullable = false)
    private String historialMedico;

    // CAMBIO: Hecho NOT NULL y tipo cambiado
    @Column(name = "fecha_ingreso_refugio", nullable = false)
    private LocalDate fechaIngresoRefugio;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
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

    // CAMBIO: Hecho NOT NULL
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tamano_id", nullable = false)
    private Tamano tamano;

    // CAMBIO: Hecho NOT NULL
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nivel_energia_id", nullable = false)
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
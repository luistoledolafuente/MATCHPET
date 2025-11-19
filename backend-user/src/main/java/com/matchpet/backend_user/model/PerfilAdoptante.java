package com.matchpet.backend_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate; // <-- ¡CRÍTICO! Debe ser LocalDate.

@Data
@Entity
@Table(name = "Perfil_Adoptante")
public class PerfilAdoptante {

    @Id
    @Column(name = "usuario_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Esto le dice a JPA que 'id' es PK y FK
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    // --- CAMPOS CORREGIDOS Y FINALIZADOS ---
    // El campo DEBE SER NOT NULL para coincidir con la DB y la lógica del DTO
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    // ¡NUEVO! Este campo es usado en tu DTO y servicio, debe estar en la Entidad.
    // Asumimos que la columna en la BD se llama 'pais'.
    @Column(name = "pais", length = 100)
    private String pais;
}
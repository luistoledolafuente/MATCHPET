package com.matchpet.backend_user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Date;

@Data
@Entity
@Table(name = "Perfil_Adoptante")
public class PerfilAdoptante {

    @Id
    @Column(name = "usuario_id") // Esta es la Clave Primaria
    private Integer id;

    /**
     * Esta es la "relación dueña".
     * Le dice a JPA que el campo 'id' de esta tabla
     * es también la Clave Foránea que apunta a UserModel.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Esto le dice a JPA que 'id' es PK y FK
    @JoinColumn(name = "usuario_id")
    @JsonIgnore // Evita bucles infinitos al convertir a JSON
    @ToString.Exclude // Evita bucles infinitos en logs
    @EqualsAndHashCode.Exclude // Evita bucles infinitos en comparaciones
    private UserModel user;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(length = 100)
    private String ciudad;
}
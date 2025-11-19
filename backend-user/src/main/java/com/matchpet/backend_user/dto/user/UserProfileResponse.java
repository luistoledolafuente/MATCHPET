package com.matchpet.backend_user.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {

    private Integer usuarioId;
    private String email;

    // (Campos separados en lugar de 'nombreCompleto')
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;

    private String telefono;
    private Set<String> roles;

    // (Añadimos los campos del perfil para una respuesta completa)
    private LocalDate fechaNacimiento;
    private String direccion;
    private String ciudad;
    private String pais;

    // (Puedes añadir más campos del perfil si los necesitas)
}
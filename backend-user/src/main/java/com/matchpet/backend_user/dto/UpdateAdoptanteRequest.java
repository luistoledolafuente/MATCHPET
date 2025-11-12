package com.matchpet.backend_user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdoptanteRequest {

    // --- Datos de UserModel ---
    @NotBlank(message = "El nombre completo es requerido")
    private String nombreCompleto;

    @NotBlank(message = "El teléfono es requerido")
    private String telefono;

    // --- Datos de PerfilAdoptante ---
    private Date fechaNacimiento; // Formato: "YYYY-MM-DD"
    private String direccion;
    private String ciudad;

    // TODO: Aquí se podrían añadir las "preferencias de mascota" de la HU-05
}
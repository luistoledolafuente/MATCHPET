package com.matchpet.backend_user.dto.refugio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRefugioRequest {

    // --- Datos para la entidad Usuario (Cuenta de Login) ---

    @NotBlank(message = "El email de login no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String emailLogin;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    // --- Datos para la entidad Refugio (Perfil Público) ---

    @NotBlank(message = "El nombre del refugio es requerido")
    private String nombreRefugio;

    @NotBlank(message = "La dirección del refugio es requerida")
    private String direccion;

    @NotBlank(message = "La ciudad del refugio es requerida")
    private String ciudad;

    @NotBlank(message = "El email público del refugio es requerido")
    @Email(message = "Debe ser un email de refugio válido")
    private String emailRefugio;

    @NotBlank(message = "El nombre de la persona de contacto es requerido")
    private String personaContacto;

    @NotBlank(message = "El teléfono de contacto es requerido")
    private String telefonoContacto;

}
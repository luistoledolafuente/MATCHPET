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

    // --- Campos de LOGIN ---
    @NotBlank(message = "El email de login es requerido")
    @Email(message = "Formato de email inválido")
    private String emailLogin;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    // --- Campos del Refugio (Entidad Refugios) ---
    @NotBlank(message = "El nombre del refugio es requerido")
    private String nombreRefugio;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion; // <-- AÑADIDO: Campo que faltaba en el DTO

    @NotBlank(message = "La dirección es requerida")
    private String direccion;

    @NotBlank(message = "La ciudad es requerida")
    private String ciudad;

    @NotBlank(message = "El país es requerido")
    private String pais; // <-- AÑADIDO: Campo que faltaba en el DTO

    @NotBlank(message = "El email de contacto es requerido")
    @Email(message = "Formato de email inválido")
    private String emailRefugio;

    @NotBlank(message = "La persona de contacto es requerida")
    private String personaContacto;

    @NotBlank(message = "El teléfono de contacto es requerido")
    private String telefonoContacto;

    // Asumimos que URL es opcional (si es NOT NULL en DB, se necesita @NotBlank)
    private String urlSitioWeb; // <-- AÑADIDO: Campo que faltaba en el DTO
}
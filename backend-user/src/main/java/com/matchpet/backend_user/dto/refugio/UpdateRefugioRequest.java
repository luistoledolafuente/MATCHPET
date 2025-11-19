package com.matchpet.backend_user.dto.refugio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRefugioRequest {

    @NotBlank(message = "El nombre del refugio es requerido")
    private String nombre;

    // --- CAMPOS AÑADIDOS/CORREGIDOS PARA CONSISTENCIA DE LA BD ---
    @NotBlank(message = "La descripción del refugio es requerida")
    private String descripcion;

    @NotBlank(message = "El país es requerido")
    private String pais;

    @NotBlank(message = "La URL del sitio web es requerida")
    private String urlSitioWeb;
    // -----------------------------------------------------------

    @NotBlank(message = "La dirección del refugio es requerida")
    private String direccion;

    @NotBlank(message = "La ciudad del refugio es requerida")
    private String ciudad;

    @NotBlank(message = "El email público del refugio es requerido")
    @Email(message = "Debe ser un email de refugio válido")
    private String email;

    @NotBlank(message = "El nombre de la persona de contacto es requerido")
    private String personaContacto;

    @NotBlank(message = "El teléfono de contacto es requerido")
    private String telefono;
}
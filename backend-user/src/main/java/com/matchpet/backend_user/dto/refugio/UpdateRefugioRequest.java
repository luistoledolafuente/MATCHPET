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

    // Nota: No incluimos emailLogin o password.
    // Esas son parte del 'Usuario', no del 'Refugio'.
    // Esta HU-06 es solo para actualizar los datos de contacto.

    @NotBlank(message = "El nombre del refugio es requerido")
    private String nombre;

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
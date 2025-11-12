package com.matchpet.backend_user.dto; // Asegúrate que sea tu paquete 'dto'

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "El token es requerido")
    private String token;

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    // Aquí podrías añadir más validaciones (mayúsculas, números) si quisieras
    private String newPassword;
}
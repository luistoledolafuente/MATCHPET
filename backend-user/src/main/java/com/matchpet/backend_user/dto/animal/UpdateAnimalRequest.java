package com.matchpet.backend_user.dto.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnimalRequest {

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotBlank(message = "La descripción de personalidad es requerida")
    private String descripcionPersonalidad;

    @NotBlank(message = "El historial médico es requerido")
    private String historialMedico;

    @NotNull(message = "La raza es requerida")
    private Integer razaId;

    @NotNull(message = "El género es requerido")
    private Integer generoId;

    @NotNull(message = "El estado de adopción es requerido")
    private Integer estadoAdopcionId;

    @NotNull(message = "El tamaño es requerido")
    private Integer tamanoId;

    @NotNull(message = "El nivel de energía es requerido")
    private Integer nivelEnergiaId;

    @NotNull(message = "La fecha de nacimiento aproximada es requerida")
    private LocalDate fechaNacimientoAprox;

    @NotNull(message = "La fecha de ingreso al refugio es requerida")
    private LocalDate fechaIngresoRefugio;

    private boolean compatibleNiños;
    private boolean compatibleOtrasMascotas;
    private boolean estaVacunado;
    private boolean estaEsterilizado;

    @NotEmpty(message = "Debe tener al menos un temperamento")
    private Set<Integer> temperamentosIds;

    @NotEmpty(message = "Debe tener al menos una foto")
    @Size(min = 1, max = 5, message = "Puedes subir entre 1 y 5 fotos")
    private List<String> fotosUrls;

    // CORRECCIÓN: Añadido @Builder.Default para evitar la advertencia
    @Builder.Default
    private Integer fotoPrincipalIndex = 0;
}
package com.matchpet.backend_user.dto.animal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnimalRequest {

    // (Es idéntico al CreateAnimalRequest,
    // pero es buena práctica mantenerlos separados)

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @NotNull(message = "La raza es requerida")
    private Integer razaId;

    @NotNull(message = "El género es requerido")
    private Integer generoId;

    @NotNull(message = "El estado de adopción es requerido")
    private Integer estadoAdopcionId;

    private Integer tamanoId;
    private Integer nivelEnergiaId;

    @NotEmpty(message = "Debe tener al menos un temperamento")
    private Set<Integer> temperamentosIds;

    @NotEmpty(message = "Debe tener al menos una foto")
    @Size(min = 1, max = 5, message = "Puedes subir entre 1 y 5 fotos")
    private List<String> fotosUrls;

    private Integer fotoPrincipalIndex = 0;

    private Date fechaNacimientoAprox;
    private String descripcionPersonalidad;
    private Boolean compatibleNiños;
    private Boolean compatibleOtrasMascotas;
    private Boolean estaVacunado;
    private Boolean estaEsterilizado;
    private String historialMedico;
    private Date fechaIngresoRefugio;
}
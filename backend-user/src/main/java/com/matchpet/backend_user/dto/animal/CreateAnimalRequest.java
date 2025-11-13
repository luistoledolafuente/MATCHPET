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
public class CreateAnimalRequest {

    // Nota: No pedimos 'refugioId' aquí.
    // Lo tomaremos automáticamente del usuario (Refugio) que esté autenticado.

    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    // --- IDs de las tablas de consulta (lookups) ---
    // (El frontend las obtendrá de /api/lookups/razas, /api/lookups/generos, etc.)

    @NotNull(message = "La raza es requerida")
    private Integer razaId;

    @NotNull(message = "El género es requerido")
    private Integer generoId;

    @NotNull(message = "El estado de adopción es requerido")
    private Integer estadoAdopcionId;

    private Integer tamanoId;

    private Integer nivelEnergiaId;

    // --- Listas de IDs ---

    @NotEmpty(message = "Debe tener al menos un temperamento")
    private Set<Integer> temperamentosIds;

    @NotEmpty(message = "Debe tener al menos una foto")
    @Size(min = 1, max = 5, message = "Puedes subir entre 1 y 5 fotos")
    private List<String> fotosUrls; // Lista de URLs (String)

    private Integer fotoPrincipalIndex = 0; // Opcional: El índice (0-4) de la foto principal

    // --- Campos Opcionales ---

    private Date fechaNacimientoAprox; // Formato: "YYYY-MM-DD"
    private String descripcionPersonalidad;
    private Boolean compatibleNiños = false;
    private Boolean compatibleOtrasMascotas = false;
    private Boolean estaVacunado = false;
    private Boolean estaEsterilizado = false;
    private String historialMedico;
    private Date fechaIngresoRefugio; // Formato: "YYYY-MM-DD"
}
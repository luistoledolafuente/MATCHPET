package com.matchpet.backend_user.dto.animal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CAMBIO: Importación moderna para fechas
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDTO {

    private Integer animal_id; // El ServiceImpl mapea animal.getId() a este campo
    private String nombre;
    private Integer refugioId; // <--- ¡Faltaba esto!

    // CAMBIO: Tipo de dato actualizado
    private LocalDate fechaNacimientoAprox;

    private String descripcionPersonalidad;

    // CAMBIO: Tipo de dato actualizado a primitivo
    private boolean compatibleNiños;
    private boolean compatibleOtrasMascotas;
    private boolean estaVacunado;
    private boolean estaEsterilizado;

    private String historialMedico;

    // CAMBIO: Tipo de dato actualizado
    private LocalDate fechaIngresoRefugio;

    private String raza;
    private String especie;
    private String genero;
    private String tamano;
    private String nivelEnergia;
    private String estadoAdopcion;
    private String refugioNombre;
    private String refugioCiudad;
    private List<String> temperamentos;
    private List<String> fotos;

    // CAMBIO: Se eliminó el constructor public AnimalDTO(Animal animal)
    // Era redundante con @Builder y contenía la lógica obsoleta
    // que causaba los errores de tipo de dato.
    // Tu AnimalServiceImpl usa el patrón Builder, que es el correcto.
}
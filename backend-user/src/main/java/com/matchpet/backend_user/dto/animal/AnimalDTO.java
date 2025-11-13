package com.matchpet.backend_user.dto.animal;

import com.matchpet.backend_user.model.AnimalFoto;
import com.matchpet.backend_user.model.Raza;
import com.matchpet.backend_user.model.Temperamento;
import com.matchpet.backend_user.model.lookup.EstadoAdopcion;
import com.matchpet.backend_user.model.lookup.Genero;
import com.matchpet.backend_user.model.lookup.NivelEnergia;
import com.matchpet.backend_user.model.lookup.Tamano;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;

// Este DTO representa un Animal "completo" para mostrar en el frontend
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDTO {

    private Integer id;
    private String nombre;
    private Date fechaNacimientoAprox;
    private String descripcionPersonalidad;
    private Boolean compatibleNiños;
    private Boolean compatibleOtrasMascotas;
    private Boolean estaVacunado;
    private Boolean estaEsterilizado;
    private String historialMedico;
    private Date fechaIngresoRefugio;

    // --- Objetos completos en lugar de IDs ---
    private Raza raza;
    private Genero genero;
    private Tamano tamano;
    private NivelEnergia nivelEnergia;
    private EstadoAdopcion estadoAdopcion;
    private Set<Temperamento> temperamentos;
    private Set<AnimalFoto> fotos;

    // (No incluimos 'refugio' para evitar bucles)
}
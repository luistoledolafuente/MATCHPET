package com.matchpet.backend_user.dto.animal;

import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.AnimalFoto;
import com.matchpet.backend_user.model.Temperamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDTO {

    private Integer animal_id;
    private String nombre;
    private Date fechaNacimientoAprox;
    private String descripcionPersonalidad;
    private Boolean compatibleNiños;
    private Boolean compatibleOtrasMascotas;
    private Boolean estaVacunado;
    private Boolean estaEsterilizado;
    private String historialMedico;
    private Date fechaIngresoRefugio;
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

    public AnimalDTO(Animal animal) {
        this.animal_id = animal.getAnimal_id(); // Funciona gracias al Arreglo 1
        this.nombre = animal.getNombre();
        this.fechaNacimientoAprox = animal.getFechaNacimientoAprox();

        this.compatibleNiños = animal.getCompatibleNiños();

        this.compatibleOtrasMascotas = animal.getCompatibleOtrasMascotas();
        this.estaVacunado = animal.getEstaVacunado();
        this.estaEsterilizado = animal.getEstaEsterilizado();
        this.descripcionPersonalidad = animal.getDescripcionPersonalidad();
        this.historialMedico = animal.getHistorialMedico();
        this.fechaIngresoRefugio = animal.getFechaIngresoRefugio();
        this.raza = animal.getRaza().getNombreRaza();
        this.especie = animal.getRaza().getEspecie().getNombreEspecie();
        this.genero = animal.getGenero().getNombre();
        this.tamano = (animal.getTamano() != null) ? animal.getTamano().getNombre() : null;
        this.nivelEnergia = (animal.getNivelEnergia() != null) ? animal.getNivelEnergia().getNombre() : null;
        this.estadoAdopcion = animal.getEstadoAdopcion().getNombre();
        this.refugioNombre = animal.getRefugio().getNombre();
        this.refugioCiudad = animal.getRefugio().getCiudad();
        this.fotos = animal.getFotos().stream()
                .map(AnimalFoto::getUrlFoto)
                .collect(Collectors.toList());
        this.temperamentos = animal.getTemperamentos().stream()
                .map(Temperamento::getNombreTemperamento)
                .collect(Collectors.toList());
    }
}
package com.matchpet.backend_user.controller;

import com.matchpet.backend_user.model.Raza;
import com.matchpet.backend_user.model.Temperamento;
import com.matchpet.backend_user.model.lookup.*; // Importa todas las clases de lookup
import com.matchpet.backend_user.repository.*; // Importa todos los repositorios
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookups") // ¡Ruta base para todas las tablas de consulta!
@RequiredArgsConstructor
@Tag(name = "5. Datos de Consulta (Lookups)", description = "Endpoints para obtener listas (para formularios)")
public class LookupController {

    // Inyectamos TODOS los repositorios de consulta que creamos
    private final GeneroRepository generoRepository;
    private final TamanoRepository tamanoRepository;
    private final NivelEnergiaRepository nivelEnergiaRepository;
    private final EstadoAdopcionRepository estadoAdopcionRepository;
    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final TemperamentoRepository temperamentoRepository;

    @Operation(summary = "Obtiene todos los géneros")
    @GetMapping("/generos")
    public ResponseEntity<List<Genero>> getGeneros() {
        return ResponseEntity.ok(generoRepository.findAll());
    }

    @Operation(summary = "Obtiene todos los tamaños")
    @GetMapping("/tamanos")
    public ResponseEntity<List<Tamano>> getTamanos() {
        return ResponseEntity.ok(tamanoRepository.findAll());
    }

    @Operation(summary = "Obtiene todos los niveles de energía")
    @GetMapping("/niveles-energia")
    public ResponseEntity<List<NivelEnergia>> getNivelesEnergia() {
        return ResponseEntity.ok(nivelEnergiaRepository.findAll());
    }

    @Operation(summary = "Obtiene todos los estados de adopción")
    @GetMapping("/estados-adopcion")
    public ResponseEntity<List<EstadoAdopcion>> getEstadosAdopcion() {
        return ResponseEntity.ok(estadoAdopcionRepository.findAll());
    }

    @Operation(summary = "Obtiene todas las especies")
    @GetMapping("/especies")
    public ResponseEntity<List<Especie>> getEspecies() {
        return ResponseEntity.ok(especieRepository.findAll());
    }

    @Operation(summary = "Obtiene todas las razas")
    @GetMapping("/razas")
    public ResponseEntity<List<Raza>> getRazas() {
        return ResponseEntity.ok(razaRepository.findAll());
    }

    @Operation(summary = "Obtiene todos los temperamentos")
    @GetMapping("/temperamentos")
    public ResponseEntity<List<Temperamento>> getTemperamentos() {
        return ResponseEntity.ok(temperamentoRepository.findAll());
    }
}
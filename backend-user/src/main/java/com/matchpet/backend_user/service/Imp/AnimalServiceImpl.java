package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;
import com.matchpet.backend_user.model.*; // Importación general
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    // (Repositorios - sin cambios)
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final RazaRepository razaRepository;
    private final GeneroRepository generoRepository;
    private final TamanoRepository tamanoRepository;
    private final NivelEnergiaRepository nivelEnergiaRepository;
    private final EstadoAdopcionRepository estadoAdopcionRepository;
    private final TemperamentoRepository temperamentoRepository;
    private final AnimalFotoRepository animalFotoRepository;

    // (Método createAnimal - sin cambios)
    @Override
    @Transactional
    public AnimalDTO createAnimal(CreateAnimalRequest request, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no está asociado a ningún refugio.");
        }
        var raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        var genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        var estadoAdopcion = estadoAdopcionRepository.findById(request.getEstadoAdopcionId())
                .orElseThrow(() -> new RuntimeException("Estado de adopción no encontrado"));
        var tamano = request.getTamanoId() != null ? tamanoRepository.findById(request.getTamanoId()).orElse(null) : null;
        var nivelEnergia = request.getNivelEnergiaId() != null ? nivelEnergiaRepository.findById(request.getNivelEnergiaId()).orElse(null) : null;
        Set<Temperamento> temperamentos = new HashSet<>(temperamentoRepository.findAllById(request.getTemperamentosIds()));
        if (temperamentos.size() != request.getTemperamentosIds().size()) {
            throw new RuntimeException("Uno o más temperamentos no fueron encontrados");
        }
        Animal animal = new Animal();
        animal.setNombre(request.getNombre());
        animal.setFechaNacimientoAprox(request.getFechaNacimientoAprox());
        animal.setDescripcionPersonalidad(request.getDescripcionPersonalidad());
        animal.setCompatibleNiños(request.getCompatibleNiños());
        animal.setCompatibleOtrasMascotas(request.getCompatibleOtrasMascotas());
        animal.setEstaVacunado(request.getEstaVacunado());
        animal.setEstaEsterilizado(request.getEstaEsterilizado());
        animal.setHistorialMedico(request.getHistorialMedico());
        animal.setFechaIngresoRefugio(request.getFechaIngresoRefugio());
        animal.setRefugio(refugio);
        animal.setRaza(raza);
        animal.setGenero(genero);
        animal.setEstadoAdopcion(estadoAdopcion);
        animal.setTamano(tamano);
        animal.setNivelEnergia(nivelEnergia);
        animal.setTemperamentos(temperamentos);
        AtomicInteger index = new AtomicInteger(0);
        Set<AnimalFoto> fotos = request.getFotosUrls().stream().map(url -> {
            AnimalFoto foto = new AnimalFoto();
            foto.setUrlFoto(url);
            foto.setAnimal(animal);
            foto.setEsPrincipal(index.getAndIncrement() == request.getFotoPrincipalIndex());
            return foto;
        }).collect(Collectors.toSet());
        animal.setFotos(fotos);
        Animal animalGuardado = animalRepository.save(animal);
        return convertToDTO(animalGuardado);
    }

    // (getAnimalesByRefugio - sin cambios)
    @Override
    @Transactional(readOnly = true)
    public List<AnimalDTO> getAnimalesByRefugio(String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no está asociado a ningún refugio.");
        }
        List<Animal> animales = animalRepository.findByRefugio(refugio);
        return animales.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // (updateAnimal - sin cambios)
    @Override
    @Transactional
    public AnimalDTO updateAnimal(Integer animalId, UpdateAnimalRequest request, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("No autorizado: Este usuario no es un refugio.");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado con id: " + animalId));
        if (!animal.getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: No tienes permiso para editar este animal.");
        }
        var raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        var genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        var estadoAdopcion = estadoAdopcionRepository.findById(request.getEstadoAdopcionId())
                .orElseThrow(() -> new RuntimeException("Estado de adopción no encontrado"));
        var tamano = request.getTamanoId() != null ? tamanoRepository.findById(request.getTamanoId()).orElse(null) : null;
        var nivelEnergia = request.getNivelEnergiaId() != null ? nivelEnergiaRepository.findById(request.getNivelEnergiaId()).orElse(null) : null;
        Set<Temperamento> temperamentos = new HashSet<>(temperamentoRepository.findAllById(request.getTemperamentosIds()));

        animal.setNombre(request.getNombre());
        animal.setFechaNacimientoAprox(request.getFechaNacimientoAprox());
        animal.setDescripcionPersonalidad(request.getDescripcionPersonalidad());
        animal.setCompatibleNiños(request.getCompatibleNiños());
        animal.setCompatibleOtrasMascotas(request.getCompatibleOtrasMascotas());
        animal.setEstaVacunado(request.getEstaVacunado());
        animal.setEstaEsterilizado(request.getEstaEsterilizado());
        animal.setHistorialMedico(request.getHistorialMedico());
        animal.setFechaIngresoRefugio(request.getFechaIngresoRefugio());

        animal.setRaza(raza);
        animal.setGenero(genero);
        animal.setEstadoAdopcion(estadoAdopcion);
        animal.setTamano(tamano);
        animal.setNivelEnergia(nivelEnergia);
        animal.setTemperamentos(temperamentos);

        animal.getFotos().clear();
        AtomicInteger index = new AtomicInteger(0);
        Set<AnimalFoto> fotosNuevas = request.getFotosUrls().stream().map(url -> {
            AnimalFoto foto = new AnimalFoto();
            foto.setUrlFoto(url);
            foto.setAnimal(animal);
            foto.setEsPrincipal(index.getAndIncrement() == request.getFotoPrincipalIndex());
            return foto;
        }).collect(Collectors.toSet());
        animal.getFotos().addAll(fotosNuevas);

        Animal animalActualizado = animalRepository.save(animal);
        return convertToDTO(animalActualizado);
    }

    // (deleteAnimal - sin cambios)
    @Override
    @Transactional
    public void deleteAnimal(Integer animalId, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("No autorizado: Este usuario no es un refugio.");
        }
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado con id: " + animalId));
        if (!animal.getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: No tienes permiso para eliminar este animal.");
        }
        animalRepository.delete(animal);
    }


    // --- ¡¡MÉTODO HELPER 100% CORREGIDO!! ---
    private AnimalDTO convertToDTO(Animal animal) {
        return AnimalDTO.builder()
                .animal_id(animal.getAnimal_id()) // <-- Corrección de .id a .animal_id
                .nombre(animal.getNombre())
                .fechaNacimientoAprox(animal.getFechaNacimientoAprox())
                .descripcionPersonalidad(animal.getDescripcionPersonalidad())
                .compatibleNiños(animal.getCompatibleNiños())
                .compatibleOtrasMascotas(animal.getCompatibleOtrasMascotas())
                .estaVacunado(animal.getEstaVacunado())
                .estaEsterilizado(animal.getEstaEsterilizado())
                .historialMedico(animal.getHistorialMedico())
                .fechaIngresoRefugio(animal.getFechaIngresoRefugio())

                // --- ¡ARREGLOS! Convertimos objetos a Strings ---
                .raza(animal.getRaza().getNombreRaza())
                .especie(animal.getRaza().getEspecie().getNombreEspecie())
                .genero(animal.getGenero().getNombre())
                .tamano(animal.getTamano() != null ? animal.getTamano().getNombre() : null)
                .nivelEnergia(animal.getNivelEnergia() != null ? animal.getNivelEnergia().getNombre() : null)
                .estadoAdopcion(animal.getEstadoAdopcion().getNombre())
                .refugioNombre(animal.getRefugio().getNombre())
                .refugioCiudad(animal.getRefugio().getCiudad())

                // --- ¡ARREGLOS! Convertimos Sets a Listas de Strings ---
                .temperamentos(animal.getTemperamentos().stream()
                        .map(Temperamento::getNombreTemperamento)
                        .collect(Collectors.toList()))
                .fotos(animal.getFotos().stream()
                        .map(AnimalFoto::getUrlFoto)
                        .collect(Collectors.toList()))
                // --- FIN DE ARREGLOS ---
                .build();
    }
}
package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;
import com.matchpet.backend_user.exception.AnimalNotFoundException;
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // --- Repositorios ---
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final RazaRepository razaRepository;
    private final GeneroRepository generoRepository;
    private final TamanoRepository tamanoRepository;
    private final NivelEnergiaRepository nivelEnergiaRepository;
    private final EstadoAdopcionRepository estadoAdopcionRepository;
    private final TemperamentoRepository temperamentoRepository;
    private final AnimalFotoRepository animalFotoRepository;

    // --- Métodos de la Interfaz ---

    @Override
    @Transactional
    public AnimalDTO createAnimal(CreateAnimalRequest request, String userEmail) {
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no está asociado a ningún refugio.");
        }

        // --- Búsqueda de Entidades (Lookups) ---
        // (El DTO ya validó que los IDs no son nulos)

        var raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        var genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        var estadoAdopcion = estadoAdopcionRepository.findById(request.getEstadoAdopcionId())
                .orElseThrow(() -> new RuntimeException("Estado de adopción no encontrado"));

        // CAMBIO: Lógica estricta. Si el DTO lo pasó, debe existir.
        var tamano = tamanoRepository.findById(request.getTamanoId())
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado con ID: " + request.getTamanoId()));
        var nivelEnergia = nivelEnergiaRepository.findById(request.getNivelEnergiaId())
                .orElseThrow(() -> new RuntimeException("Nivel de energía no encontrado con ID: " + request.getNivelEnergiaId()));

        Set<Temperamento> temperamentos = new HashSet<>(temperamentoRepository.findAllById(request.getTemperamentosIds()));
        if (temperamentos.size() != request.getTemperamentosIds().size()) {
            throw new RuntimeException("Uno o más temperamentos no fueron encontrados");
        }

        // --- Creación y Mapeo de la Entidad Animal ---
        Animal animal = new Animal();
        animal.setNombre(request.getNombre());
        animal.setFechaNacimientoAprox(request.getFechaNacimientoAprox()); // DTO usa LocalDate
        animal.setDescripcionPersonalidad(request.getDescripcionPersonalidad());
        animal.setHistorialMedico(request.getHistorialMedico());
        animal.setFechaIngresoRefugio(request.getFechaIngresoRefugio()); // DTO usa LocalDate

        // CAMBIO: Usar 'is...' para booleanos primitivos
        animal.setCompatibleNiños(request.isCompatibleNiños());
        animal.setCompatibleOtrasMascotas(request.isCompatibleOtrasMascotas());
        animal.setEstaVacunado(request.isEstaVacunado());
        animal.setEstaEsterilizado(request.isEstaEsterilizado());

        // Asignación de relaciones
        animal.setRefugio(refugio);
        animal.setRaza(raza);
        animal.setGenero(genero);
        animal.setEstadoAdopcion(estadoAdopcion);
        animal.setTamano(tamano); // Ahora nunca será null
        animal.setNivelEnergia(nivelEnergia); // Ahora nunca será null
        animal.setTemperamentos(temperamentos);

        // Lógica de Fotos (sin cambios, estaba bien)
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
                .orElseThrow(() -> new AnimalNotFoundException("Animal no encontrado con id: " + animalId));

        if (!animal.getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: No tienes permiso para editar este animal.");
        }

        // --- Búsqueda de Entidades (Lookups) ---
        var raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        var genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        var estadoAdopcion = estadoAdopcionRepository.findById(request.getEstadoAdopcionId())
                .orElseThrow(() -> new RuntimeException("Estado de adopción no encontrado"));

        // CAMBIO: Lógica estricta
        var tamano = tamanoRepository.findById(request.getTamanoId())
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));
        var nivelEnergia = nivelEnergiaRepository.findById(request.getNivelEnergiaId())
                .orElseThrow(() -> new RuntimeException("Nivel de energía no encontrado"));

        Set<Temperamento> temperamentos = new HashSet<>(temperamentoRepository.findAllById(request.getTemperamentosIds()));

        // --- Actualización de campos ---
        animal.setNombre(request.getNombre());
        animal.setFechaNacimientoAprox(request.getFechaNacimientoAprox());
        animal.setDescripcionPersonalidad(request.getDescripcionPersonalidad());
        animal.setHistorialMedico(request.getHistorialMedico());
        animal.setFechaIngresoRefugio(request.getFechaIngresoRefugio());

        // CAMBIO: Usar 'is...' para booleanos primitivos
        animal.setCompatibleNiños(request.isCompatibleNiños());
        animal.setCompatibleOtrasMascotas(request.isCompatibleOtrasMascotas());
        animal.setEstaVacunado(request.isEstaVacunado());
        animal.setEstaEsterilizado(request.isEstaEsterilizado());

        animal.setRaza(raza);
        animal.setGenero(genero);
        animal.setEstadoAdopcion(estadoAdopcion);
        animal.setTamano(tamano);
        animal.setNivelEnergia(nivelEnergia);
        animal.setTemperamentos(temperamentos);

        // Lógica de Fotos (sin cambios, estaba bien)
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
                .orElseThrow(() -> new AnimalNotFoundException("Animal no encontrado con id: " + animalId));

        if (!animal.getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: No tienes permiso para eliminar este animal.");
        }
        animalRepository.delete(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public AnimalDTO getAnimalById(Integer id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new AnimalNotFoundException("No se encontró el animal con ID: " + id));
        return convertToDTO(animal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnimalDTO> getAnimalesPaginados(Pageable pageable) {
        Page<Animal> animalesPaginados = animalRepository.findAll(pageable);
        return animalesPaginados.map(this::convertToDTO);
    }


    // --- Métodos Privados Helper ---

    private AnimalDTO convertToDTO(Animal animal) {
        return AnimalDTO.builder()
                .animal_id(animal.getId()) // CAMBIO: Usar .getId()
                .nombre(animal.getNombre())
                .fechaNacimientoAprox(animal.getFechaNacimientoAprox()) // Es LocalDate
                .descripcionPersonalidad(animal.getDescripcionPersonalidad())
                .compatibleNiños(animal.isCompatibleNiños()) // CAMBIO: Usar 'is...'
                .compatibleOtrasMascotas(animal.isCompatibleOtrasMascotas())
                .estaVacunado(animal.isEstaVacunado())
                .estaEsterilizado(animal.isEstaEsterilizado())
                .historialMedico(animal.getHistorialMedico())
                .fechaIngresoRefugio(animal.getFechaIngresoRefugio()) // Es LocalDate

                .raza(animal.getRaza().getNombreRaza())
                .especie(animal.getRaza().getEspecie().getNombreEspecie())
                .genero(animal.getGenero().getNombre())

                // CAMBIO: Ya no se necesita la comprobación '!= null'
                .tamano(animal.getTamano().getNombre())
                .nivelEnergia(animal.getNivelEnergia().getNombre())

                .estadoAdopcion(animal.getEstadoAdopcion().getNombre())
                .refugioNombre(animal.getRefugio().getNombre())

                // CAMBIO: Corregido el typo 'getCidad()'
                .refugioCiudad(animal.getRefugio().getCiudad())

                .temperamentos(animal.getTemperamentos().stream()
                        .map(Temperamento::getNombreTemperamento)
                        .collect(Collectors.toList()))
                .fotos(animal.getFotos().stream()
                        .map(AnimalFoto::getUrlFoto)
                        .collect(Collectors.toList()))
                .build();
    }
}
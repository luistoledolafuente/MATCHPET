package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.AnimalFoto;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.Temperamento;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {

    // Inyectamos TODOS los repositorios que necesitamos
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final RazaRepository razaRepository;
    private final GeneroRepository generoRepository;
    private final TamanoRepository tamanoRepository;
    private final NivelEnergiaRepository nivelEnergiaRepository;
    private final EstadoAdopcionRepository estadoAdopcionRepository;
    private final TemperamentoRepository temperamentoRepository;

    @Override
    @Transactional
    public Animal createAnimal(CreateAnimalRequest request, String userEmail) {

        // 1. Encontrar al usuario y su refugio
        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = user.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no está asociado a ningún refugio.");
        }

        // 2. Buscar todas las entidades "Lookup" por sus IDs
        var raza = razaRepository.findById(request.getRazaId())
                .orElseThrow(() -> new RuntimeException("Raza no encontrada"));
        var genero = generoRepository.findById(request.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        var estadoAdopcion = estadoAdopcionRepository.findById(request.getEstadoAdopcionId())
                .orElseThrow(() -> new RuntimeException("Estado de adopción no encontrado"));

        // Buscamos los opcionales
        var tamano = request.getTamanoId() != null ? tamanoRepository.findById(request.getTamanoId()).orElse(null) : null;
        var nivelEnergia = request.getNivelEnergiaId() != null ? nivelEnergiaRepository.findById(request.getNivelEnergiaId()).orElse(null) : null;

        // 3. Buscar el Set de Temperamentos
        Set<Temperamento> temperamentos = new HashSet<>(temperamentoRepository.findAllById(request.getTemperamentosIds()));
        if (temperamentos.size() != request.getTemperamentosIds().size()) {
            throw new RuntimeException("Uno o más temperamentos no fueron encontrados");
        }

        // 4. Crear la entidad Animal
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

        // 5. Enlazar las entidades
        animal.setRefugio(refugio);
        animal.setRaza(raza);
        animal.setGenero(genero);
        animal.setEstadoAdopcion(estadoAdopcion);
        animal.setTamano(tamano);
        animal.setNivelEnergia(nivelEnergia);
        animal.setTemperamentos(temperamentos);

        // 6. Crear y enlazar las fotos
        AtomicInteger index = new AtomicInteger(0);
        Set<AnimalFoto> fotos = request.getFotosUrls().stream().map(url -> {
            AnimalFoto foto = new AnimalFoto();
            foto.setUrlFoto(url);
            foto.setAnimal(animal); // Enlaza la foto al animal
            // Marca la foto principal (por defecto la primera, índice 0)
            foto.setEsPrincipal(index.getAndIncrement() == request.getFotoPrincipalIndex());
            return foto;
        }).collect(Collectors.toSet());

        animal.setFotos(fotos);

        // 7. Guardar en la Base de Datos
        // Gracias a CascadeType.ALL en la entidad Animal,
        // esto guardará el Animal Y todas sus AnimalFotos al mismo tiempo.
        return animalRepository.save(animal);
    }
}
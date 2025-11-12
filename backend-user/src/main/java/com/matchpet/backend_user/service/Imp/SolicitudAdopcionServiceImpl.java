package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.adoptante.AdoptanteInfoDTO;
import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.solicitud.CreateSolicitudRequest;
import com.matchpet.backend_user.dto.solicitud.SolicitudResponseDTO;
import com.matchpet.backend_user.dto.solicitud.UpdateSolicitudRequest;
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.SolicitudAdopcion;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.model.lookup.EstadoSolicitud;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.SolicitudAdopcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    // Inyectamos todos los repositorios que necesitamos
    private final SolicitudAdopcionRepository solicitudRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;

    // --- ACCIONES DEL ADOPTANTE ---

    @Override
    @Transactional
    public SolicitudResponseDTO createSolicitud(CreateSolicitudRequest request, String adoptanteEmail) {
        // 1. Buscar al adoptante
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));

        // 2. Buscar el animal
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));

        // 3. Buscar el estado inicial ("Enviada", ID 1 según tu script SQL)
        EstadoSolicitud estadoInicial = estadoSolicitudRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado 'Enviada' no encontrado en la BD."));

        // 4. Crear la solicitud
        SolicitudAdopcion nuevaSolicitud = new SolicitudAdopcion();
        nuevaSolicitud.setAdoptante(adoptante);
        nuevaSolicitud.setAnimal(animal);
        nuevaSolicitud.setEstadoSolicitud(estadoInicial);

        // 5. Guardar y convertir a DTO
        SolicitudAdopcion solicitudGuardada = solicitudRepository.save(nuevaSolicitud);
        return convertToDTO(solicitudGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getMisSolicitudes(String adoptanteEmail) {
        // 1. Buscar al adoptante
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));

        // 2. Buscar en el repo y convertir la lista a DTOs
        return solicitudRepository.findByAdoptante(adoptante).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- ACCIONES DEL REFUGIO ---

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getSolicitudesRecibidas(String refugioEmail) {
        // 1. Buscar el refugio
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }

        // 2. Buscar en el repo por el ID del refugio y convertir
        return solicitudRepository.findByAnimal_Refugio_Id(refugio.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolicitudResponseDTO updateSolicitud(Integer solicitudId, UpdateSolicitudRequest request, String refugioEmail) {
        // 1. Buscar el refugio (para seguridad)
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }

        // 2. Buscar la solicitud
        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 3. ¡VERIFICACIÓN DE SEGURIDAD!
        // Nos aseguramos de que el animal de esta solicitud pertenezca al refugio logueado.
        if (!solicitud.getAnimal().getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: Esta solicitud no pertenece a su refugio.");
        }

        // 4. Buscar el nuevo estado
        EstadoSolicitud nuevoEstado = estadoSolicitudRepository.findById(request.getEstadoSolicitudId())
                .orElseThrow(() -> new RuntimeException("El nuevo estado no es válido."));

        // 5. Actualizar los campos
        solicitud.setEstadoSolicitud(nuevoEstado);
        solicitud.setNotasInternas(request.getNotasInternas());
        solicitud.setMensajeAlAdoptante(request.getMensajeAlAdoptante());

        // 6. Guardar y devolver DTO
        SolicitudAdopcion solicitudActualizada = solicitudRepository.save(solicitud);
        return convertToDTO(solicitudActualizada);
    }


    // --- MAPPERS PRIVADOS ---
    // (Estos métodos convierten las Entidades en DTOs seguros para el frontend)

    private SolicitudResponseDTO convertToDTO(SolicitudAdopcion solicitud) {
        return SolicitudResponseDTO.builder()
                .id(solicitud.getId())
                .fechaSolicitud(solicitud.getFechaSolicitud())
                .fechaActualizacion(solicitud.getFechaActualizacion())
                .notasInternas(solicitud.getNotasInternas())
                .mensajeAlAdoptante(solicitud.getMensajeAlAdoptante())
                .estadoSolicitud(solicitud.getEstadoSolicitud())
                .animal(convertAnimalToDTO(solicitud.getAnimal()))
                .adoptante(convertAdoptanteToDTO(solicitud.getAdoptante()))
                .build();
    }

    private AdoptanteInfoDTO convertAdoptanteToDTO(UserModel adoptante) {
        return AdoptanteInfoDTO.builder()
                .usuarioId(adoptante.getId())
                .email(adoptante.getEmail())
                .nombreCompleto(adoptante.getNombreCompleto())
                .telefono(adoptante.getTelefono())
                .perfil(adoptante.getAdoptante()) // Obtenemos el perfil de adoptante
                .build();
    }

    private AnimalDTO convertAnimalToDTO(Animal animal) {
        // (Este es el mismo mapper que usamos en AnimalServiceImpl)
        return AnimalDTO.builder()
                .id(animal.getId())
                .nombre(animal.getNombre())
                .fechaNacimientoAprox(animal.getFechaNacimientoAprox())
                .descripcionPersonalidad(animal.getDescripcionPersonalidad())
                .compatibleNiños(animal.getCompatibleNiños())
                .compatibleOtrasMascotas(animal.getCompatibleOtrasMascotas())
                .estaVacunado(animal.getEstaVacunado())
                .estaEsterilizado(animal.getEstaEsterilizado())
                .historialMedico(animal.getHistorialMedico())
                .fechaIngresoRefugio(animal.getFechaIngresoRefugio())
                .raza(animal.getRaza())
                .genero(animal.getGenero())
                .tamano(animal.getTamano())
                .nivelEnergia(animal.getNivelEnergia())
                .estadoAdopcion(animal.getEstadoAdopcion())
                .temperamentos(animal.getTemperamentos())
                .fotos(animal.getFotos())
                .build();
    }
}
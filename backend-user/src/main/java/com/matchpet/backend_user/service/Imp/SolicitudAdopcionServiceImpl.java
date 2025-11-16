package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.adoptante.AdoptanteInfoDTO;
import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.solicitud.CreateSolicitudRequest;
import com.matchpet.backend_user.dto.solicitud.SolicitudResponseDTO;
import com.matchpet.backend_user.dto.solicitud.UpdateSolicitudRequest;
import com.matchpet.backend_user.model.*; // Importación general
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

    // (Repositorios - sin cambios)
    private final SolicitudAdopcionRepository solicitudRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;

    // (createSolicitud - sin cambios)
    @Override
    @Transactional
    public SolicitudResponseDTO createSolicitud(CreateSolicitudRequest request, String adoptanteEmail) {
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));
        EstadoSolicitud estadoInicial = estadoSolicitudRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado 'Enviada' no encontrado en la BD."));
        SolicitudAdopcion nuevaSolicitud = new SolicitudAdopcion();
        nuevaSolicitud.setAdoptante(adoptante);
        nuevaSolicitud.setAnimal(animal);
        nuevaSolicitud.setEstadoSolicitud(estadoInicial);
        SolicitudAdopcion solicitudGuardada = solicitudRepository.save(nuevaSolicitud);
        return convertToDTO(solicitudGuardada);
    }

    // (getMisSolicitudes - sin cambios)
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getMisSolicitudes(String adoptanteEmail) {
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));
        return solicitudRepository.findByAdoptante(adoptante).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // (getSolicitudesRecibidas - sin cambios)
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getSolicitudesRecibidas(String refugioEmail) {
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }
        return solicitudRepository.findByAnimal_Refugio_Id(refugio.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // (updateSolicitud - sin cambios)
    @Override
    @Transactional
    public SolicitudResponseDTO updateSolicitud(Integer solicitudId, UpdateSolicitudRequest request, String refugioEmail) {
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }
        SolicitudAdopcion solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        if (!solicitud.getAnimal().getRefugio().getId().equals(refugio.getId())) {
            throw new RuntimeException("No autorizado: Esta solicitud no pertenece a su refugio.");
        }
        EstadoSolicitud nuevoEstado = estadoSolicitudRepository.findById(request.getEstadoSolicitudId())
                .orElseThrow(() -> new RuntimeException("El nuevo estado no es válido."));
        solicitud.setEstadoSolicitud(nuevoEstado);
        solicitud.setNotasInternas(request.getNotasInternas());
        solicitud.setMensajeAlAdoptante(request.getMensajeAlAdoptante());
        SolicitudAdopcion solicitudActualizada = solicitudRepository.save(solicitud);
        return convertToDTO(solicitudActualizada);
    }


    // --- MAPPERS PRIVADOS ---

    // (convertToDTO - sin cambios)
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

    // (convertAdoptanteToDTO - sin cambios)
    private AdoptanteInfoDTO convertAdoptanteToDTO(UserModel adoptante) {
        return AdoptanteInfoDTO.builder()
                .usuarioId(adoptante.getId())
                .email(adoptante.getEmail())
                .nombreCompleto(adoptante.getNombreCompleto())
                .telefono(adoptante.getTelefono())
                .perfil(adoptante.getAdoptante())
                .build();
    }

    // --- ¡¡MÉTODO HELPER 100% CORREGIDO!! ---
    private AnimalDTO convertAnimalToDTO(Animal animal) {
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
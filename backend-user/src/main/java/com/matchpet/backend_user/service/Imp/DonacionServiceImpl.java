package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.donacion.CheckoutResponseDTO;
import com.matchpet.backend_user.dto.donacion.CreateDonacionRequest;
import com.matchpet.backend_user.dto.donacion.DonacionResponseDTO;
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.model.lookup.EstadoPago;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.DonacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonacionServiceImpl implements DonacionService {

    // Inyectamos todos los repositorios que necesitamos
    private final DonacionRepository donacionRepository;
    private final DonanteRepository donanteRepository;
    private final UserRepository userRepository;
    private final RefugioRepository refugioRepository;
    private final AnimalRepository animalRepository;
    private final EstadoPagoRepository estadoPagoRepository; // Para el estado "Pendiente"

    @Override
    @Transactional
    public CheckoutResponseDTO createDonacion(CreateDonacionRequest request, String userEmail) {

        // 1. Encontrar o crear al Donante
        Donante donante = findOrCreateDonante(request, userEmail);

        // 2. Encontrar el destino (si se especificó)
        Refugio refugio = (request.getRefugioId() != null)
                ? refugioRepository.findById(request.getRefugioId()).orElse(null)
                : null;

        Animal animal = (request.getAnimalId() != null)
                ? animalRepository.findById(request.getAnimalId()).orElse(null)
                : null;

        // 3. Obtener el estado de pago "Pendiente" (ID 1 según tu script SQL)
        EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Error: Estado de pago 'Pendiente' (ID 1) no encontrado en la BD."));

        // 4. Crear la entidad Donacion
        Donacion donacion = new Donacion();
        donacion.setMonto(request.getMonto());
        donacion.setMoneda(request.getMoneda());
        donacion.setMensajeDonante(request.getMensajeDonante());
        donacion.setDonante(donante);
        donacion.setRefugio(refugio);
        donacion.setAnimal(animal);
        donacion.setEstadoPago(estadoPendiente);

        // 5. Guardar la donación en la BD (para obtener su ID)
        Donacion donacionGuardada = donacionRepository.save(donacion);

        // 6. --- SIMULACIÓN DE PASARELA DE PAGO ---
        // En un proyecto real, aquí llamaríamos a la API de Culqi o Stripe
        // con los datos de la donación (monto, moneda, ID) y nos
        // devolverían una URL o un ID de sesión.
        // Por ahora, simulamos uno.
        String checkoutSessionId = "fake_checkout_session_" + donacionGuardada.getId() + "_" + System.currentTimeMillis();

        // 7. Devolvemos el ID de la sesión de pago al frontend
        return new CheckoutResponseDTO(checkoutSessionId, donacionGuardada.getId());
    }

    /**
     * Lógica para encontrar un donante.
     * Si el usuario está logueado, lo busca por su UserModel.
     * Si es un invitado, lo busca por su email.
     * Si no existe, lo crea.
     */
    private Donante findOrCreateDonante(CreateDonacionRequest request, String userEmail) {
        // Opción 1: El usuario está logueado
        if (userEmail != null) {
            UserModel user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Busca si ya tiene un perfil de donante
            return donanteRepository.findByUser(user).orElseGet(() -> {
                // Si no, crea uno nuevo enlazado a su cuenta
                Donante nuevoDonante = new Donante();
                nuevoDonante.setNombreCompleto(user.getNombreCompleto());
                nuevoDonante.setEmail(user.getEmail());
                nuevoDonante.setUser(user);
                return donanteRepository.save(nuevoDonante);
            });
        }

        // Opción 2: El usuario es un invitado (no logueado)
        if (request.getEmailDonante() == null || request.getNombreDonante() == null) {
            throw new RuntimeException("Para donar como invitado, se requiere nombre y email.");
        }

        // Busca si ya ha donado antes con ese email
        return donanteRepository.findByEmail(request.getEmailDonante()).orElseGet(() -> {
            // Si no, crea un nuevo donante (sin UserModel)
            Donante nuevoDonanteInvitado = new Donante();
            nuevoDonanteInvitado.setNombreCompleto(request.getNombreDonante());
            nuevoDonanteInvitado.setEmail(request.getEmailDonante());
            // (nuevoDonanteInvitado.user es null, lo cual es correcto)
            return donanteRepository.save(nuevoDonanteInvitado);
        });
    }


    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponseDTO> getDonacionesByRefugio(String refugioEmail) {
        // 1. Buscar el refugio
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }

        // 2. Buscar en el repo y convertir la lista a DTOs
        return donacionRepository.findByRefugio(refugio).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponseDTO> getMisDonaciones(String adoptanteEmail) {
        // 1. Buscar al usuario
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));

        // 2. Encontrar su perfil de donante (si existe)
        Optional<Donante> donanteOpt = donanteRepository.findByUser(adoptante);
        if (donanteOpt.isEmpty()) {
            return Collections.emptyList(); // No ha hecho donaciones
        }

        // 3. Buscar sus donaciones y convertir a DTO
        return donacionRepository.findByDonante(donanteOpt.get()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Método helper privado para convertir Donacion (Entidad) a DonacionResponseDTO
     */
    private DonacionResponseDTO convertToDTO(Donacion donacion) {
        return DonacionResponseDTO.builder()
                .id(donacion.getId())
                .monto(donacion.getMonto())
                .moneda(donacion.getMoneda())
                .fechaDonacion(donacion.getFechaDonacion())
                .mensajeDonante(donacion.getMensajeDonante())
                .estadoPago(donacion.getEstadoPago())
                .nombreDonante(donacion.getDonante().getNombreCompleto())
                // Maneja valores nulos (donación general vs. donación específica)
                .nombreRefugio(donacion.getRefugio() != null ? donacion.getRefugio().getNombre() : "Donación General")
                .nombreAnimal(donacion.getAnimal() != null ? donacion.getAnimal().getNombre() : null)
                .build();
    }
}
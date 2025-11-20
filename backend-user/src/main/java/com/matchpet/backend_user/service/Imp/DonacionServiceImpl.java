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

    private final DonacionRepository donacionRepository;
    private final DonanteRepository donanteRepository;
    private final UserRepository userRepository;
    private final RefugioRepository refugioRepository;
    private final AnimalRepository animalRepository;
    private final EstadoPagoRepository estadoPagoRepository;

    @Override
    @Transactional
    public CheckoutResponseDTO createDonacion(CreateDonacionRequest request, String userEmail) {

        Donante donante = findOrCreateDonante(request, userEmail);

        Refugio refugio = (request.getRefugioId() != null)
                ? refugioRepository.findById(request.getRefugioId()).orElse(null)
                : null;

        Animal animal = (request.getAnimalId() != null)
                ? animalRepository.findById(request.getAnimalId()).orElse(null)
                : null;

        EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Error: Estado de pago 'Pendiente' (ID 1) no encontrado en la BD."));

        Donacion donacion = new Donacion();
        donacion.setMonto(request.getMonto());
        donacion.setMoneda(request.getMoneda());
        donacion.setMensajeDonante(request.getMensajeDonante());
        donacion.setDonante(donante);
        donacion.setRefugio(refugio);
        donacion.setAnimal(animal);
        donacion.setEstadoPago(estadoPendiente);

        Donacion donacionGuardada = donacionRepository.save(donacion);

        String checkoutSessionId = "fake_checkout_session_" + donacionGuardada.getId() + "_" + System.currentTimeMillis();

        return new CheckoutResponseDTO(checkoutSessionId, donacionGuardada.getId());
    }

    /**
     * Lógica para encontrar un donante.
     */
    private Donante findOrCreateDonante(CreateDonacionRequest request, String userEmail) {
        // Opción 1: El usuario está logueado
        if (userEmail != null) {
            UserModel user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            return donanteRepository.findByUser(user).orElseGet(() -> {
                Donante nuevoDonante = new Donante();
                // CORRECCIÓN: Uso de getNombreCompleto() para el donante logueado
                nuevoDonante.setNombreCompleto(user.getNombreCompleto());
                nuevoDonante.setEmail(user.getEmail());
                nuevoDonante.setUser(user);
                return donanteRepository.save(nuevoDonante);
            });
        }

        // Opción 2: El usuario es un invitado
        if (request.getEmailDonante() == null || request.getNombreDonante() == null) {
            throw new RuntimeException("Para donar como invitado, se requiere nombre y email.");
        }

        return donanteRepository.findByEmail(request.getEmailDonante()).orElseGet(() -> {
            Donante nuevoDonanteInvitado = new Donante();
            nuevoDonanteInvitado.setNombreCompleto(request.getNombreDonante());
            nuevoDonanteInvitado.setEmail(request.getEmailDonante());
            return donanteRepository.save(nuevoDonanteInvitado);
        });
    }


    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponseDTO> getDonacionesByRefugio(String refugioEmail) {
        UserModel userRefugio = userRepository.findByEmail(refugioEmail)
                .orElseThrow(() -> new RuntimeException("Usuario refugio no encontrado"));
        Refugio refugio = userRefugio.getRefugio();
        if (refugio == null) {
            throw new RuntimeException("Este usuario no es un refugio.");
        }

        return donacionRepository.findByRefugio(refugio).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonacionResponseDTO> getMisDonaciones(String adoptanteEmail) {
        UserModel adoptante = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario adoptante no encontrado"));

        Optional<Donante> donanteOpt = donanteRepository.findByUser(adoptante);
        if (donanteOpt.isEmpty()) {
            return Collections.emptyList();
        }

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
                .nombreRefugio(donacion.getRefugio() != null ? donacion.getRefugio().getNombre() : "Donación General")
                .nombreAnimal(donacion.getAnimal() != null ? donacion.getAnimal().getNombre() : null)
                .build();
    }
}
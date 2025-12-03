package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.donacion.CheckoutResponseDTO;
import com.matchpet.backend_user.dto.donacion.CreateDonacionRequest;
import com.matchpet.backend_user.dto.donacion.DonacionResponseDTO;
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.model.lookup.EstadoPago;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.DonacionService;

// --- IMPORTS DE MERCADO PAGO ---
import com.mercadopago.exceptions.MPApiException; // <--- IMPORTANTE
import com.mercadopago.exceptions.MPException;    // <--- IMPORTANTE
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

        // 1. Lógica de negocio previa (Guardar en BD)
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

        // 2. Integración con Mercado Pago
        try {
            // A. Crear el ítem a cobrar
            // NOTA: unitPrice espera BigDecimal. Si request.getMonto() es Double/Integer, úsalo directo si el tipo coincide,
            // o usa 'new BigDecimal(request.getMonto().toString())' para mayor seguridad.
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("DONACION-" + donacionGuardada.getId())
                    .title("Donación a MatchPet")
                    .description("Ayuda voluntaria para refugios")
                    .pictureUrl("https://matchpet.org/logo.png")
                    .quantity(1)
                    .currencyId("PEN")
                    .unitPrice(new BigDecimal(request.getMonto().toString())) // <--- Aseguramos conversión a BigDecimal
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // B. Configurar a dónde vuelve el usuario después de pagar
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/dashboard/adoptante/donaciones?status=success")
                    .pending("http://localhost:5173/dashboard/adoptante/donaciones?status=pending")
                    .failure("http://localhost:5173/dashboard/adoptante/donaciones?status=failure")
                    .build();

            // C. Crear la solicitud de preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    /*.autoReturn("approved")*/
                    .externalReference(String.valueOf(donacionGuardada.getId()))
                    .build();

            // D. Llamar a Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // 3. Retornar los datos reales al Frontend
            return CheckoutResponseDTO.builder()
                    .preferenceId(preference.getId())
                    .url(preference.getInitPoint())
                    .donacionId(donacionGuardada.getId())
                    .build();

        } catch (MPApiException e) {
            // ESTO ES LO NUEVO: Imprimimos el contenido exacto del error que manda MercadoPago
            System.err.println("❌ ERROR MP API (Detalle): " + e.getApiResponse().getContent());
            e.printStackTrace();
            throw new RuntimeException("Error MP API: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            System.err.println("❌ Error General: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error General: " + e.getMessage());
        }
    }

    private Donante findOrCreateDonante(CreateDonacionRequest request, String userEmail) {
        if (userEmail != null) {
            UserModel user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            return donanteRepository.findByUser(user).orElseGet(() -> {
                Donante nuevoDonante = new Donante();
                nuevoDonante.setNombreCompleto(user.getNombreCompleto());
                nuevoDonante.setEmail(user.getEmail());
                nuevoDonante.setUser(user);
                return donanteRepository.save(nuevoDonante);
            });
        }

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
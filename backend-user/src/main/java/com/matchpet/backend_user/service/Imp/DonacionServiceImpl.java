package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.donacion.CheckoutResponseDTO;
import com.matchpet.backend_user.dto.donacion.CreateDonacionRequest;
import com.matchpet.backend_user.dto.donacion.DonacionResponseDTO;
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.model.lookup.EstadoPago;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.DonacionService;

// --- IMPORTS DE MERCADO PAGO ---
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.client.preference.PreferencePayerRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // <--- Importante para @Value
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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

    // --- VARIABLES DE ENTORNO (Configurables en application.yml) ---
    @Value("${app.frontend-url:http://localhost:5173}") // Valor por defecto si no existe en yml
    private String frontendUrl;

    @Value("${app.backend-domain:http://localhost:8081}") // Valor por defecto (ojo: para webhook real necesitas https pública)
    private String backendDomain;

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
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("DONACION-" + donacionGuardada.getId())
                    .title("Donación a MatchPet")
                    .description("Ayuda voluntaria para refugios")
                    .pictureUrl("https://matchpet.org/logo.png")
                    .quantity(1)
                    .currencyId("PEN")
                    .unitPrice(new BigDecimal(request.getMonto().toString()))
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // B. Configurar URLs de retorno
            String urlSuccess = frontendUrl + "/dashboard/adoptante/donaciones?status=success";
            String urlFailure = frontendUrl + "/dashboard/adoptante/donaciones?status=failure";
            String urlPending = frontendUrl + "/dashboard/adoptante/donaciones?status=pending";

            if (request.getSuccessUrl() != null && !request.getSuccessUrl().isEmpty()) urlSuccess = request.getSuccessUrl();
            if (request.getFailureUrl() != null && !request.getFailureUrl().isEmpty()) {
                urlFailure = request.getFailureUrl();
                urlPending = request.getFailureUrl();
            }

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(urlSuccess)
                    .pending(urlPending)
                    .failure(urlFailure)
                    .build();

            // --- ¡AQUÍ ESTABA EL ERROR! FALTABA CREAR ESTE OBJETO ---
            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .name(donante.getNombreCompleto())
                    .email(donante.getEmail())
                    .build();
            // --------------------------------------------------------

            // C. Crear la solicitud de preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payerRequest) // Ahora sí existe esta variable
                    .backUrls(backUrls)
                    /*.autoReturn("approved")*/
                    .externalReference(String.valueOf(donacionGuardada.getId()))
                    // IMPORTANTE: Descomenta esto y asegúrate que backendDomain sea tu URL de NGROK
                    .notificationUrl(" https://unprecipitately-electrostrictive-mabel.ngrok-free.dev/api/donaciones/webhook")
                    .build();

            // D. Llamar a Mercado Pago
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return CheckoutResponseDTO.builder()
                    .preferenceId(preference.getId())
                    .url(preference.getInitPoint())
                    .donacionId(donacionGuardada.getId())
                    .build();

        } catch (MPApiException e) {
            System.err.println("❌ ERROR MP API: " + e.getApiResponse().getContent());
            e.printStackTrace();
            throw new RuntimeException("Error MP API");
        } catch (Exception e) {
            System.err.println("❌ Error General: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error General");
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

    @Override
    @Transactional
    public void receiveWebhook(Map<String, Object> payload) {
        try {
            // 1. Validar que sea un evento de pago
            String type = (String) payload.get("type");
            // A veces MP envía "payment" en topic o type, verificamos ambos por seguridad
            if (type == null && payload.containsKey("topic")) {
                type = (String) payload.get("topic");
            }

            if (!"payment".equals(type)) {
                return; // Ignoramos otros eventos
            }

            // 2. Obtener el ID del pago
            String paymentId = null;
            if (payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                paymentId = (String) data.get("id");
            } else if (payload.containsKey("id")) {
                paymentId = (String) payload.get("id");
            }

            if (paymentId == null) return;

            // 3. Consultar a Mercado Pago el estado real
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            // 4. Buscar la donación en BD
            String externalRef = payment.getExternalReference();
            if (externalRef == null) return;

            Integer donacionId = Integer.parseInt(externalRef);
            Donacion donacion = donacionRepository.findById(donacionId)
                    .orElseThrow(() -> new RuntimeException("Donación no encontrada"));

            // --- MEJORA: Guardamos el ID de transacción de MP ---
            donacion.setGatewayTransaccionId(String.valueOf(payment.getId()));

            // 5. Actualizar el estado
            String status = payment.getStatus();
            EstadoPago nuevoEstado = null;

            if ("approved".equals(status)) {
                nuevoEstado = estadoPagoRepository.findById(2).orElse(null); // COMPLETADO
            } else if ("rejected".equals(status) || "cancelled".equals(status)) {
                nuevoEstado = estadoPagoRepository.findById(3).orElse(null); // RECHAZADO
            }

            if (nuevoEstado != null && !nuevoEstado.equals(donacion.getEstadoPago())) {
                donacion.setEstadoPago(nuevoEstado);
                donacionRepository.save(donacion);
                System.out.println("✅ Webhook: Donación #" + donacionId + " actualizada a " + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error procesando webhook: " + e.getMessage());
        }
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
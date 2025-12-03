package com.matchpet.backend_user.config;

import com.mercadopago.MercadoPagoConfig; // <--- Importante: Importar la clase del SDK
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MPConfig {

    // Asegúrate de que en application.yml la propiedad se llame exactamente así
    @Value("${mercadopago.access.token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        // CORRECCIÓN: Usamos MercadoPagoConfig (del SDK), no MPConfig (esta clase)
        MercadoPagoConfig.setAccessToken(accessToken);
        System.out.println("✅ SDK de Mercado Pago inicializado correctamente.");
    }
}
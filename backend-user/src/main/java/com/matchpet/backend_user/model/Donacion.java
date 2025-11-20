package com.matchpet.backend_user.model;

import com.matchpet.backend_user.model.lookup.EstadoPago;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Donaciones")
public class Donacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donacion_id")
    private Integer id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 3)
    private String moneda;

    @CreationTimestamp
    @Column(name = "fecha_donacion", updatable = false)
    private Timestamp fechaDonacion;

    @Column(name = "gateway_transaccion_id", unique = true)
    private String gatewayTransaccionId; // ID de la pasarela de pago (ej: Culqi, Stripe)

    @Column(name = "mensaje_donante", columnDefinition = "TEXT")
    private String mensajeDonante;

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donante_id", nullable = false)
    private Donante donante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refugio_id") // Puede ser nulo (donación general)
    private Refugio refugio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id") // Puede ser nulo (donación a un refugio)
    private Animal animal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_pago_id", nullable = false)
    private EstadoPago estadoPago;
}
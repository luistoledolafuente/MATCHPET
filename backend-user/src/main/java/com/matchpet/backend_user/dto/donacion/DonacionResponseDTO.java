package com.matchpet.backend_user.dto.donacion;

import com.matchpet.backend_user.model.lookup.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

// DTO para mostrar un resumen de la donación
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonacionResponseDTO {

    private Integer id;
    private BigDecimal monto;
    private String moneda;
    private Timestamp fechaDonacion;
    private String mensajeDonante;
    private EstadoPago estadoPago;

    // --- Info Resumida ---
    private String nombreDonante;
    private String nombreRefugio; // Nombre del refugio, si aplica
    private String nombreAnimal;  // Nombre del animal, si aplica
}
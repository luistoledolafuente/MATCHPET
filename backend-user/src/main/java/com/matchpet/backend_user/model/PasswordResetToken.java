package com.matchpet.backend_user.model; // Asegúrate que sea tu paquete 'model'

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PasswordResetTokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;

    /**
     * Un token le pertenece a Un Usuario.
     * Usamos FetchType.EAGER porque siempre que cargamos un token,
     * queremos saber inmediatamente de qué usuario es.
     */
    @OneToOne(targetEntity = UserModel.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id", unique = true)
    private UserModel user;

    public PasswordResetToken(String token, Timestamp expiryDate, UserModel user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }
}
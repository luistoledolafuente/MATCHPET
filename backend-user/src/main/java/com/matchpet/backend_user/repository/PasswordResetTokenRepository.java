package com.matchpet.backend_user.repository; // Asegúrate que sea tu paquete 'repository'

import com.matchpet.backend_user.model.PasswordResetToken;
import com.matchpet.backend_user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token por su cadena de texto.
     * Lo usaremos para validar el token que nos llega del frontend.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Busca si un usuario ya tiene un token.
     * Lo usaremos para borrar tokens viejos si el usuario pide uno nuevo.
     */
    Optional<PasswordResetToken> findByUser(UserModel user);

}
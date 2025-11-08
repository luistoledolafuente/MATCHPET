package com.matchpet.backend_user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Traemos el email "de" (from) desde el application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Envía un email de reseteo de contraseña.
     * La anotación @Async hace que se ejecute en un hilo separado (en segundo plano).
     * ¡Esto es crucial para que la API responda rápido!
     *
     * @param to    El email del destinatario.
     * @param token El token de reseteo (ej: "a1b2-c3d4-e5f6")
     */
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Tu Solicitud de Reseteo de Contraseña - MatchPet");

            // TODO: Cuando tengas tu frontend, reemplaza esta URL
            String resetUrl = "http://localhost:3000/reset-password?token=" + token;

            message.setText("Hola,\n\n"
                    + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                    + "Haz clic en el siguiente enlace (o pégalo en tu navegador) para completar el proceso:\n"
                    + resetUrl + "\n\n"
                    + "Si no solicitaste esto, por favor ignora este email.\n\n"
                    + "Gracias,\n"
                    + "El equipo de MatchPet");

            mailSender.send(message);

        } catch (Exception e) {
            // Manejo de error (en un proyecto real, aquí iría un log más robusto)
            System.err.println("Error al enviar email de reseteo a " + to + ": " + e.getMessage());
        }
    }
}
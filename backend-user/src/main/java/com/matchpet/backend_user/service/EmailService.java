package com.matchpet.backend_user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Método para enviar el correo de restablecimiento de contraseña
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("luismiguel.tlf@gmail.com");  // Tu correo de Gmail
        message.setTo(to);
        message.setSubject("Reseteo de Contraseña");

        // Enlace de restablecimiento de contraseña
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        message.setText("Haz clic en el siguiente enlace para resetear tu contraseña: " + resetUrl);
        mailSender.send(message);
    }
}

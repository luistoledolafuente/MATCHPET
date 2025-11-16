//package com.matchpet.backend_user;
//
//import com.matchpet.backend_user.dto.auth.ForgotPasswordRequest;
//import com.matchpet.backend_user.dto.auth.ResetPasswordRequest;
//import com.matchpet.backend_user.model.PasswordResetToken;
//import com.matchpet.backend_user.model.RolModel;
//import com.matchpet.backend_user.model.UserModel;
//import com.matchpet.backend_user.repository.PasswordResetTokenRepository;
//import com.matchpet.backend_user.repository.RolRepository;
//import com.matchpet.backend_user.repository.UserRepository;
//import com.matchpet.backend_user.service.AuthService;
//import com.matchpet.backend_user.service.EmailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.Timestamp;
//import java.util.HashSet; // <-- ¡Importante!
//import java.util.Set;     // <-- ¡Importante!
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//@Transactional
//public class PasswordResetIntegrationTest {
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RolRepository rolRepository;
//
//    @Autowired
//    private PasswordResetTokenRepository tokenRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private EmailService emailService;
//
//    private UserModel testUser;
//    private final String userEmail = "test@example.com";
//    private final String userPassword = "password123";
//
//    @BeforeEach
//    void setUp() {
//        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());
//
//        // 1. Buscar el rol "Adoptante".
//        RolModel adoptanteRole = rolRepository.findByNombreRol("Adoptante")
//                .orElse(null);
//
//        // 2. Si no existe, crear una instancia NUEVA (sin guardar)
//        if (adoptanteRole == null) {
//            adoptanteRole = new RolModel(null, "Adoptante");
//        }
//
//        // --- ¡CORRECCIÓN AQUÍ! ---
//        // 3. Crear el usuario de prueba
//        Set<RolModel> roles = new HashSet<>();
//        roles.add(adoptanteRole);
//
//        testUser = UserModel.builder()
//                .email(userEmail)
//                .hashContrasena(passwordEncoder.encode(userPassword))
//                .nombreCompleto("Test User")
//                .estaActivo(true)
//                .roles(roles) // <-- Se usa el HashSet mutable
//                .fechaCreacionPerfil(new Timestamp(System.currentTimeMillis()))
//                .fechaActualizacion(new Timestamp(System.currentTimeMillis()))
//                .build();
//        // --- FIN DE LA CORRECCIÓN ---
//
//        // 4. Guardar el usuario.
//        userRepository.save(testUser);
//    }
//
//    @Test
//    void testForgotPassword_Success() {
//        // (Sin cambios)
//        ForgotPasswordRequest request = new ForgotPasswordRequest();
//        request.setEmail(userEmail);
//        authService.forgotPassword(request);
//        assertTrue(tokenRepository.findByUser(testUser).isPresent(), "El token debería haberse creado en la BD");
//        verify(emailService).sendPasswordResetEmail(anyString(), anyString());
//    }
//
//    @Test
//    void testForgotPassword_UserNotFound() {
//        // (Sin cambios)
//        ForgotPasswordRequest request = new ForgotPasswordRequest();
//        request.setEmail("noexiste@example.com");
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            authService.forgotPassword(request);
//        });
//        assertEquals("Usuario no encontrado con email: noexiste@example.com", exception.getMessage());
//    }
//
//    @Test
//    void testResetPassword_Success() {
//        // (Sin cambios)
//        String tokenStr = UUID.randomUUID().toString();
//        Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + 3600000); // 1 hora
//        PasswordResetToken token = new PasswordResetToken(tokenStr, expiryDate, testUser);
//        tokenRepository.save(token);
//        String newPassword = "nuevaPassword456";
//        ResetPasswordRequest request = new ResetPasswordRequest();
//        request.setToken(tokenStr);
//        request.setNewPassword(newPassword);
//        authService.resetPassword(request);
//        assertFalse(tokenRepository.findByToken(tokenStr).isPresent(), "El token debería borrarse después de usarse");
//        UserModel updatedUser = userRepository.findByEmail(userEmail).get();
//        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getHashContrasena()), "La contraseña nueva debe coincidir");
//        assertFalse(passwordEncoder.matches(userPassword, updatedUser.getHashContrasena()), "La contraseña antigua no debe coincidir");
//    }
//
//    @Test
//    void testResetPassword_InvalidToken() {
//        // (Sin cambios)
//        ResetPasswordRequest request = new ResetPasswordRequest();
//        request.setToken("token-falso-123");
//        request.setNewPassword("nuevaPassword456");
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            authService.resetPassword(request);
//        });
//        assertEquals("Token de reseteo inválido", exception.getMessage());
//    }
//
//    @Test
//    void testResetPassword_ExpiredToken() {
//        // (Sin cambios)
//        String tokenStr = UUID.randomUUID().toString();
//        Timestamp expiryDate = new Timestamp(System.currentTimeMillis() - 10000); // Expiró hace 10 seg
//        PasswordResetToken token = new PasswordResetToken(tokenStr, expiryDate, testUser);
//        tokenRepository.save(token);
//        ResetPasswordRequest request = new ResetPasswordRequest();
//        request.setToken(tokenStr);
//        request.setNewPassword("nuevaPassword456");
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            authService.resetPassword(request);
//        });
//        assertEquals("Token de reseteo expirado", exception.getMessage());
//        assertFalse(tokenRepository.findByToken(tokenStr).isPresent(), "El token expirado debería borrarse");
//    }
//}
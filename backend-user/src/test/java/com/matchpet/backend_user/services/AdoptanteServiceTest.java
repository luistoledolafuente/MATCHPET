package com.matchpet.backend_user.services;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.service.Imp.AdoptanteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdoptanteServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdoptanteServiceImpl adoptanteService;

    private RegisterAdoptanteRequest request;

    @BeforeEach
    void setUp() {
        // Preparación de datos de entrada para pruebas
        request = new RegisterAdoptanteRequest(
                "test@example.com", "password123", "John", "Doe", "Smith",
                "1234567890", LocalDate.now(), "Some Address", "Some City", "Some Country"
        );
    }

    // Test para registrar un adoptante exitoso
    @Test
    void testRegisterAdoptante_Success() {
        // Act: Llamar al método registerAdoptante
        AuthResponse response = adoptanteService.registerAdoptante(request);

        // Assert: Comprobamos que el token de acceso no sea nulo y el mensaje sea el esperado
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    // Test cuando el email ya existe en la base de datos
    @Test
    void testRegisterAdoptante_EmailAlreadyExists() {
        // Simulamos un email que ya existe en la base de datos
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setHashContrasena("hashedPassword");
        userRepository.save(user);

        // Act & Assert: Esperamos que se lance una excepción de tipo RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> adoptanteService.registerAdoptante(request));
        assertEquals("El email ya está registrado", exception.getMessage());
    }
}

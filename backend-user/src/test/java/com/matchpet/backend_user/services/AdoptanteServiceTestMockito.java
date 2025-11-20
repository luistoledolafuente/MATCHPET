package com.matchpet.backend_user.services;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.service.Imp.AdoptanteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class AdoptanteServiceTestMockito {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
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

    // Test para registrar un adoptante exitoso usando Mockito
    @Test
    void testRegisterAdoptante_Success() {
        // Simulamos que no hay usuario con el mismo email
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Adoptante")).thenReturn(Optional.of(new RolModel()));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");

        // Act: Llamar al método registerAdoptante
        AuthResponse response = adoptanteService.registerAdoptante(request);

        // Assert: Verificamos que la respuesta contiene un token
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    // Test cuando el email ya existe en la base de datos usando Mockito
    @Test
    void testRegisterAdoptante_EmailAlreadyExists() {
        // Simulamos que ya existe un usuario con el mismo email
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new UserModel()));

        // Act & Assert: Esperamos que se lance una excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> adoptanteService.registerAdoptante(request));
        assertEquals("El email ya está registrado", exception.getMessage());
    }

    // Test cuando el rol 'Adoptante' no se encuentra usando Mockito
    @Test
    void testRegisterAdoptante_RoleNotFound() {
        // Simulamos que no existe el rol 'Adoptante'
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Adoptante")).thenReturn(Optional.empty());

        // Act & Assert: Esperamos que se lance una excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> adoptanteService.registerAdoptante(request));
        assertEquals("Error: Rol 'Adoptante' no encontrado.", exception.getMessage());
    }
}

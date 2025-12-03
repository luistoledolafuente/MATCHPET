package com.matchpet.backend_user.services;

import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.adoptante.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.PerfilAdoptanteRepository;
import com.matchpet.backend_user.repository.AnimalRepository;
import com.matchpet.backend_user.service.AnimalService;
import com.matchpet.backend_user.service.Imp.AdoptanteServiceImpl;
import com.matchpet.backend_user.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class AdoptanteServiceTestMockito {

    private UserRepository userRepository;
    private RolRepository rolRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private PerfilAdoptanteRepository adoptanteRepository;
    private AnimalRepository animalRepository;     // <-- NUEVO
    private AnimalService animalService;           // <-- NUEVO

    private AdoptanteServiceImpl adoptanteService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        rolRepository = mock(RolRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        adoptanteRepository = mock(PerfilAdoptanteRepository.class);

        animalRepository = mock(AnimalRepository.class); // <-- NUEVO
        animalService = mock(AnimalService.class);       // <-- NUEVO

        adoptanteService = new AdoptanteServiceImpl(
                userRepository,
                rolRepository,
                passwordEncoder,
                jwtService,
                adoptanteRepository,
                animalRepository,  // <-- NUEVO
                animalService      // <-- NUEVO
        );
    }

    @Test
    void testRegisterAdoptanteSuccess() {
        RegisterAdoptanteRequest request = new RegisterAdoptanteRequest();
        request.setEmail("a@a.com");
        request.setPassword("1234");

        RolModel rol = new RolModel();
        rol.setNombreRol("Adoptante");

        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Adoptante")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("1234")).thenReturn("ENCRYPTED");
        when(jwtService.generateAccessToken(any())).thenReturn("TOKEN1");
        when(jwtService.generateRefreshToken(any())).thenReturn("TOKEN2");

        AuthResponse response = adoptanteService.registerAdoptante(request);

        assertEquals("TOKEN1", response.getAccessToken());
        assertEquals("TOKEN2", response.getRefreshToken());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    void testRegisterAdoptante_EmailYaRegistrado() {
        RegisterAdoptanteRequest request = new RegisterAdoptanteRequest();
        request.setEmail("a@a.com");

        when(userRepository.findByEmail("a@a.com"))
                .thenReturn(Optional.of(new UserModel()));

        assertThrows(RuntimeException.class,
                () -> adoptanteService.registerAdoptante(request));
    }

    @Test
    void testRegisterAdoptante_RolNoEncontrado() {
        RegisterAdoptanteRequest request = new RegisterAdoptanteRequest();
        request.setEmail("a@a.com");

        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Adoptante"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> adoptanteService.registerAdoptante(request));
    }
}

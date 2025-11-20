package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.adoptante.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.PerfilAdoptanteRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.AdoptanteService;
import com.matchpet.backend_user.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdoptanteServiceImpl implements AdoptanteService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PerfilAdoptanteRepository adoptanteRepository;

    @Override
    @Transactional
    public AuthResponse registerAdoptante(RegisterAdoptanteRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("El email ya está registrado");
        });
        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));
        Set<RolModel> roles = new HashSet<>();
        roles.add(defaultRole);

        // 1. Crear Entidades
        UserModel user = new UserModel();
        PerfilAdoptante nuevoPerfilAdoptante = new PerfilAdoptante();

        // 2. Llenar datos de UserModel
        user.setEmail(request.getEmail());
        user.setHashContrasena(passwordEncoder.encode(request.getPassword()));
        user.setNombre(request.getNombre());
        user.setApellidoPaterno(request.getApellidoPaterno());
        user.setApellidoMaterno(request.getApellidoMaterno());
        user.setTelefono(request.getTelefono());
        user.setRoles(roles);
        user.setEstaActivo(true);
        user.setFechaCreacionPerfil(new Timestamp(System.currentTimeMillis()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        // 3. Llenar datos de PerfilAdoptante (Lógica de Mapeo)
        // ESTAS LÍNEAS SON CRÍTICAS Y YA ESTÁN CORRECTAS EN EL SERVICIO.
        nuevoPerfilAdoptante.setFechaNacimiento(request.getFechaNacimiento());
        nuevoPerfilAdoptante.setDireccion(request.getDireccion());
        nuevoPerfilAdoptante.setCiudad(request.getCiudad());
        nuevoPerfilAdoptante.setPais(request.getPais());

        // 4. Vincular ambas entidades
        user.setAdoptante(nuevoPerfilAdoptante);
        nuevoPerfilAdoptante.setUser(user);

        // 5. Guardar (JPA guardará ambas entidades gracias a la cascada)
        userRepository.save(user);

        // 6. Generar Tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateAdoptante(Integer usuarioId, UpdateAdoptanteRequest request) {

        UserModel user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        PerfilAdoptante perfil = adoptanteRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de adoptante no encontrado para el usuario: " + usuarioId));

        // Actualizar campos de UserModel
        user.setNombre(request.getNombre());
        user.setApellidoPaterno(request.getApellidoPaterno());
        user.setApellidoMaterno(request.getApellidoMaterno());
        user.setTelefono(request.getTelefono());
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        // Actualizar campos del PerfilAdoptante
        perfil.setFechaNacimiento(request.getFechaNacimiento());
        perfil.setDireccion(request.getDireccion());
        perfil.setCiudad(request.getCiudad());
        perfil.setPais(request.getPais());

        PerfilAdoptante updatedPerfil = adoptanteRepository.save(perfil);
        UserModel updatedUser = updatedPerfil.getUser();

        // Devolver el DTO de perfil con campos separados
        return UserProfileResponse.builder()
                .usuarioId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .nombre(updatedUser.getNombre())
                .apellidoPaterno(updatedUser.getApellidoPaterno())
                .apellidoMaterno(updatedUser.getApellidoMaterno())
                .telefono(updatedUser.getTelefono())
                .roles(updatedUser.getRoles().stream()
                        .map(RolModel::getNombreRol)
                        .collect(Collectors.toSet()))
                .fechaNacimiento(updatedPerfil.getFechaNacimiento())
                .direccion(updatedPerfil.getDireccion())
                .ciudad(updatedPerfil.getCiudad())
                .pais(updatedPerfil.getPais())
                .build();
    }
}
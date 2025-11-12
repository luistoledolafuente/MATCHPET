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

    // Inyectamos todas las dependencias necesarias
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PerfilAdoptanteRepository adoptanteRepository;

    @Override
    @Transactional
    public AuthResponse registerAdoptante(RegisterAdoptanteRequest request) {
        // (Esta es la lógica exacta que movimos de AuthServiceImpl,
        // usando el método 'new' que ya arreglamos)

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("El email ya está registrado");
        });
        RolModel defaultRole = rolRepository.findByNombreRol("Adoptante")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Adoptante' no encontrado."));
        Set<RolModel> roles = new HashSet<>();
        roles.add(defaultRole);

        UserModel user = new UserModel();
        PerfilAdoptante nuevoPerfilAdoptante = new PerfilAdoptante();

        user.setEmail(request.getEmail());
        user.setHashContrasena(passwordEncoder.encode(request.getPassword()));
        user.setNombreCompleto(request.getNombreCompleto());
        user.setTelefono(request.getTelefono());
        user.setRoles(roles);
        user.setEstaActivo(true);
        user.setFechaCreacionPerfil(new Timestamp(System.currentTimeMillis()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        user.setAdoptante(nuevoPerfilAdoptante);
        nuevoPerfilAdoptante.setUser(user);

        userRepository.save(user);

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
        // 1. Encontrar el Usuario (que es el "dueño" de todo)
        UserModel user = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        // 2. Encontrar el Perfil del Adoptante
        PerfilAdoptante perfil = adoptanteRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil de adoptante no encontrado para el usuario: " + usuarioId));

        // 3. Actualizar los campos del UserModel
        user.setNombreCompleto(request.getNombreCompleto());
        user.setTelefono(request.getTelefono());

        // 4. Actualizar los campos del PerfilAdoptante
        perfil.setFechaNacimiento(request.getFechaNacimiento());
        perfil.setDireccion(request.getDireccion());
        perfil.setCiudad(request.getCiudad());

        // 5. Guardar el UserModel (guardará PerfilAdoptante por cascada)
        UserModel updatedUser = userRepository.save(user);

        // 6. Devolver el DTO de perfil actualizado (igual que en UserServiceImp)
        return UserProfileResponse.builder()
                .usuarioId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .nombreCompleto(updatedUser.getNombreCompleto())
                .telefono(updatedUser.getTelefono())
                .roles(updatedUser.getRoles().stream()
                        .map(RolModel::getNombreRol)
                        .collect(Collectors.toSet()))
                .refugio(updatedUser.getRefugio())
                .adoptante(updatedUser.getAdoptante())
                .build();
    }
}
package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.PerfilAdoptante; // Necesario para la lógica
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el contexto de seguridad"));

        // Obtenemos el perfil adoptante de forma segura (puede ser nulo si es un Refugio)
        PerfilAdoptante adoptantePerfil = user.getAdoptante();

        return UserProfileResponse.builder()
                .usuarioId(user.getId())
                .email(user.getEmail())
                // CORRECCIÓN: Mapeo de campos individuales del UserModel
                .nombre(user.getNombre())
                .apellidoPaterno(user.getApellidoPaterno())
                .apellidoMaterno(user.getApellidoMaterno())
                .telefono(user.getTelefono())
                .roles(user.getRoles().stream()
                        .map(RolModel::getNombreRol)
                        .collect(Collectors.toSet()))

                // Mapeo de campos del PerfilAdoptante (solo si existe)
                .fechaNacimiento(adoptantePerfil != null ? adoptantePerfil.getFechaNacimiento() : null)
                .direccion(adoptantePerfil != null ? adoptantePerfil.getDireccion() : null)
                .ciudad(adoptantePerfil != null ? adoptantePerfil.getCiudad() : null)
                // Asumo que tu PerfilAdoptante tiene un campo 'pais' o que el país por defecto es Perú
                .pais("Perú")

                .build();
    }
}
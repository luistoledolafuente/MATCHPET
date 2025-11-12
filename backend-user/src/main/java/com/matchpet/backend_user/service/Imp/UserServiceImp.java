package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.RolModel; // ¡Importante!
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors; // ¡Importante!

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

        // --- ¡LÓGICA ACTUALIZADA PARA HU-07! ---

        return UserProfileResponse.builder()

                .usuarioId(user.getId())
                .email(user.getEmail())
                .nombreCompleto(user.getNombreCompleto())
                .telefono(user.getTelefono())
                .roles(user.getRoles().stream()
                        .map(RolModel::getNombreRol)
                        .collect(Collectors.toSet()))
                // Pasamos los objetos de perfil completos
                .refugio(user.getRefugio())
                .adoptante(user.getAdoptante())
                .build();
    }
}
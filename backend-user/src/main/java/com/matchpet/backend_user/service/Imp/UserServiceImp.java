package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.user.UserProfileResponse;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.RefugioRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final RefugioRepository refugioRepository;

    // UserServiceImp.java
@Override
public UserProfileResponse getUserProfile() {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String userEmail = userDetails.getUsername();

    UserModel user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado en el contexto de seguridad"));

    PerfilAdoptante adoptantePerfil = user.getAdoptante();
    Refugio refugio = refugioRepository.findByUser(user).orElse(null);

    return UserProfileResponse.builder()
            .usuarioId(user.getId())
            .email(user.getEmail())
            .nombre(user.getNombre())
            .apellidoPaterno(user.getApellidoPaterno())
            .apellidoMaterno(user.getApellidoMaterno())
            .telefono(user.getTelefono())
            .roles(user.getRoles().stream()
                    .map(RolModel::getNombreRol)
                    .collect(Collectors.toSet()))

            // Adoptante
            .fechaNacimiento(adoptantePerfil != null ? adoptantePerfil.getFechaNacimiento() : null)
            .direccion(adoptantePerfil != null ? adoptantePerfil.getDireccion() : null)
            .ciudad(adoptantePerfil != null ? adoptantePerfil.getCiudad() : null)
            .pais(adoptantePerfil != null ? adoptantePerfil.getPais() : "Perú")

            // Refugio: TODOS los datos
            .refugio(Map.of(
                "id", refugio != null ? refugio.getId() : 0,
                "nombre", refugio != null && refugio.getNombre() != null ? refugio.getNombre() : "",
                "descripcion", refugio != null && refugio.getDescripcion() != null ? refugio.getDescripcion() : "",
                "pais", refugio != null && refugio.getPais() != null ? refugio.getPais() : "",
                "urlSitioWeb", refugio != null && refugio.getUrlSitioWeb() != null ? refugio.getUrlSitioWeb() : "",
                "direccion", refugio != null && refugio.getDireccion() != null ? refugio.getDireccion() : "",
                "ciudad", refugio != null && refugio.getCiudad() != null ? refugio.getCiudad() : "",
                "email", refugio != null && refugio.getEmail() != null ? refugio.getEmail() : "",
                "personaContacto", refugio != null && refugio.getPersonaContacto() != null ? refugio.getPersonaContacto() : "",
                "telefono", refugio != null && refugio.getTelefono() != null ? refugio.getTelefono() : ""
                ))


            .build();
}

}

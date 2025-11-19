package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.refugio.RegisterRefugioRequest;
import com.matchpet.backend_user.dto.refugio.UpdateRefugioRequest;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.RolModel;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.RefugioRepository;
import com.matchpet.backend_user.repository.RolRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.JwtService;
import com.matchpet.backend_user.service.RefugioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RefugioServiceImpl implements RefugioService {

    private final RefugioRepository refugioRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // --- Método de Registro (registerRefugio) ---
    @Override
    @Transactional
    public AuthResponse registerRefugio(RegisterRefugioRequest request) {

        userRepository.findByEmail(request.getEmailLogin()).ifPresent(user -> {
            throw new RuntimeException("El email de login ya está registrado");
        });
        refugioRepository.findByEmail(request.getEmailRefugio()).ifPresent(refugio -> {
            throw new RuntimeException("El email del refugio ya está registrado");
        });
        RolModel refugioRole = rolRepository.findByNombreRol("Refugio")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Refugio' no encontrado."));

        Refugio nuevoRefugio = new Refugio();
        UserModel user = new UserModel();
        Set<RolModel> roles = new HashSet<>();
        roles.add(refugioRole);

        nuevoRefugio.setNombre(request.getNombreRefugio());

        // Mapeo de campos que causaron error en el registro
        nuevoRefugio.setDescripcion(request.getDescripcion());
        nuevoRefugio.setPais(request.getPais());
        nuevoRefugio.setUrlSitioWeb(request.getUrlSitioWeb());

        nuevoRefugio.setDireccion(request.getDireccion());
        nuevoRefugio.setCiudad(request.getCiudad());
        nuevoRefugio.setEmail(request.getEmailRefugio());
        nuevoRefugio.setPersonaContacto(request.getPersonaContacto());
        nuevoRefugio.setTelefono(request.getTelefonoContacto());

        user.setEmail(request.getEmailLogin());
        user.setHashContrasena(passwordEncoder.encode(request.getPassword()));

        // Mapeo de nombre granular a UserModel
        String[] nombreParts = request.getPersonaContacto().split("\\s+", 3);
        user.setNombre(nombreParts.length > 0 ? nombreParts[0] : "Refugio");
        user.setApellidoPaterno(nombreParts.length > 1 ? nombreParts[1] : "");
        user.setApellidoMaterno(nombreParts.length > 2 ? nombreParts[2] : "");

        user.setTelefono(request.getTelefonoContacto());
        user.setRoles(roles);
        user.setEstaActivo(true);
        user.setFechaCreacionPerfil(new Timestamp(System.currentTimeMillis()));
        user.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        user.setRefugio(nuevoRefugio);
        nuevoRefugio.setUser(user);

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // --- Método de Actualización (updateRefugio) ---
    @Override
    @Transactional
    public Refugio updateRefugio(Integer refugioId, UpdateRefugioRequest request) {
        Refugio refugio = refugioRepository.findById(refugioId)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado con id: " + refugioId));

        // Mapeo de campos del DTO al Refugio
        refugio.setNombre(request.getNombre());
        refugio.setDireccion(request.getDireccion());
        refugio.setCiudad(request.getCiudad());
        refugio.setEmail(request.getEmail());

        // MAPEO DE CAMPOS NUEVOS/CORREGIDOS
        refugio.setDescripcion(request.getDescripcion());
        refugio.setPais(request.getPais());
        refugio.setUrlSitioWeb(request.getUrlSitioWeb());
        // FIN MAPEO

        // Si el Refugio cambia el nombre de la Persona de Contacto, actualizamos el UserModel
        if (request.getPersonaContacto() != null && !request.getPersonaContacto().equals(refugio.getPersonaContacto())) {
            UserModel user = refugio.getUser();
            String[] nombreParts = request.getPersonaContacto().split("\\s+", 3);
            user.setNombre(nombreParts.length > 0 ? nombreParts[0] : "Refugio");
            user.setApellidoPaterno(nombreParts.length > 1 ? nombreParts[1] : "");
            user.setApellidoMaterno(nombreParts.length > 2 ? nombreParts[2] : "");
            userRepository.save(user); // Guarda el cambio en UserModel
        }

        refugio.setPersonaContacto(request.getPersonaContacto());
        refugio.setTelefono(request.getTelefono());

        return refugioRepository.save(refugio);
    }
}
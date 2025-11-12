package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.RegisterRefugioRequest;
import com.matchpet.backend_user.dto.UpdateRefugioRequest;
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

    // --- ¡TODAS LAS DEPENDENCIAS QUE NECESITAMOS! ---
    private final RefugioRepository refugioRepository;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    @Transactional
    public AuthResponse registerRefugio(RegisterRefugioRequest request) {
        // (Esta es la lógica exacta que movimos de AuthServiceImpl)

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
        nuevoRefugio.setDireccion(request.getDireccion());
        nuevoRefugio.setCiudad(request.getCiudad());
        nuevoRefugio.setEmail(request.getEmailRefugio());
        nuevoRefugio.setPersonaContacto(request.getPersonaContacto());
        nuevoRefugio.setTelefono(request.getTelefonoContacto());

        user.setEmail(request.getEmailLogin());
        user.setHashContrasena(passwordEncoder.encode(request.getPassword()));
        user.setNombreCompleto(request.getPersonaContacto());
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


    @Override
    @Transactional
    public Refugio updateRefugio(Integer refugioId, UpdateRefugioRequest request) {
        // (Este es el método que ya teníamos)
        Refugio refugio = refugioRepository.findById(refugioId)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado con id: " + refugioId));

        refugio.setNombre(request.getNombre());
        refugio.setDireccion(request.getDireccion());
        refugio.setCiudad(request.getCiudad());
        refugio.setEmail(request.getEmail());
        refugio.setPersonaContacto(request.getPersonaContacto());
        refugio.setTelefono(request.getTelefono());

        return refugioRepository.save(refugio);
    }
}
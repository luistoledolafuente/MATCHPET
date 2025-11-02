package com.matchpet.backend_user.service;

// ¡Asegúrate de que esta ruta a tu UserModel sea correcta!
import com.matchpet.backend_user.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // --- ¡Leemos las NUEVAS llaves del .properties! ---
    @Value("${jwt.access.secret}")
    private String JWT_ACCESS_SECRET;
    @Value("${jwt.access.expiration}")
    private long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.refresh.secret}")
    private String JWT_REFRESH_SECRET;
    @Value("${jwt.refresh.expiration}")
    private long JWT_REFRESH_EXPIRATION;


    // --- Generación de Access Token (con claims custom) ---
    // (Este es el que usa el Login y el Register)
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Añadimos datos extra al token (si es nuestro UserModel)
        if (userDetails instanceof UserModel) {
            UserModel user = (UserModel) userDetails;
            claims.put("usuarioId", user.getUsuarioId());
            claims.put("nombre", user.getNombreCompleto());
        }
        return buildToken(claims, userDetails, JWT_ACCESS_EXPIRATION, getAccessSignInKey());
    }

    // --- NUEVO: Generación de Refresh Token (sin claims custom) ---
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, JWT_REFRESH_EXPIRATION, getRefreshSignInKey());
    }

    // Método constructor de tokens (privado)
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, Key signInKey) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // El email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Métodos de Validación (Para el Access Token) ---
    // (Estos son los que usa el JwtAuthenticationFilter)

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, getAccessSignInKey());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, getAccessSignInKey());
    }

    // --- Métodos de Validación (Para el Refresh Token) ---
    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshSignInKey());
    }


    // --- Métodos Utilitarios (Genéricos) ---
    private boolean isTokenExpired(String token, Key key) {
        try {
            return extractExpiration(token, key).before(new Date());
        } catch (Exception e) {
            return true; // Si hay error al parsear, asumimos que expiró
        }
    }

    private Date extractExpiration(String token, Key key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Key key) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Métodos para obtener las llaves (privados) ---
    private Key getAccessSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_ACCESS_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_REFRESH_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
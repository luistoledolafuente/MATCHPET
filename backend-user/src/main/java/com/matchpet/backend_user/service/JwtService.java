package com.matchpet.backend_user.service;

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
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.access.secret}")
    private String JWT_ACCESS_SECRET;

    @Value("${jwt.access.expiration}")
    private long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.refresh.secret}")
    private String JWT_REFRESH_SECRET;

    @Value("${jwt.refresh.expiration}")
    private long JWT_REFRESH_EXPIRATION;


    // --- Generación de Access Token (con claims custom y claim aleatorio) ---
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof UserModel) {
            UserModel user = (UserModel) userDetails;
            claims.put("usuarioId", user.getUsuarioId());
            claims.put("nombre", user.getNombreCompleto());
        }

        // ⚡ Claim aleatorio para garantizar que el token sea único
        claims.put("rnd", UUID.randomUUID().toString());

        return buildToken(claims, userDetails, JWT_ACCESS_EXPIRATION, getAccessSignInKey());
    }

    // --- Generación de Refresh Token (sin claims custom ni aleatorio) ---
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, JWT_REFRESH_EXPIRATION, getRefreshSignInKey());
    }

    // --- Constructor genérico de tokens ---
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, Key signInKey) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Métodos de Validación (Access Token) ---
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, getAccessSignInKey());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, getAccessSignInKey());
    }

    // --- Métodos de Validación (Refresh Token) ---
    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, getRefreshSignInKey());
    }

    // --- Utilitarios genéricos ---
    private boolean isTokenExpired(String token, Key key) {
        try {
            return extractExpiration(token, key).before(new Date());
        } catch (Exception e) {
            return true;
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Llaves privadas ---
    private Key getAccessSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_ACCESS_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_REFRESH_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

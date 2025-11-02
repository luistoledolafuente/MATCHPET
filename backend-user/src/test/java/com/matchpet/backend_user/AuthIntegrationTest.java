package com.matchpet.backend_user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.matchpet.backend_user.dto.AuthResponse;
import com.matchpet.backend_user.dto.LoginRequest;
import com.matchpet.backend_user.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullAuthenticationFlow() throws Exception {

        // --- 0. Preparación ---
        String uniqueEmail = "test-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "password123";
        String nombre = "Test User Flow";

        // --- 1. Prueba de REGISTRO (HU-01) ---
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);
        registerRequest.setNombreCompleto(nombre);
        registerRequest.setTelefono("999888777");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())  // ¡FIX! Buscamos accessToken
                .andExpect(jsonPath("$.refreshToken").exists()); // ¡FIX! Buscamos refreshToken


        // --- 2. Prueba de LOGIN (HU-02) ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // ¡FIX!
                .andExpect(jsonPath("$.refreshToken").exists()) // ¡FIX!
                .andReturn();

        // Extraemos el token del JSON de respuesta
        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String accessToken = authResponse.getAccessToken(); // ¡FIX! Usamos getAccessToken()


        // --- 3. Prueba de ENDPOINT PROTEGIDO (HU-03-Test) ---

        // 3a. Prueba que Falla (sin token)
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized()); // (Ya habíamos arreglado esto a 401)

        // 3b. Prueba Exitosa (con token)
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + accessToken)) // ¡FIX! Usamos el accessToken
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.nombreCompleto").value(nombre));
    }
}

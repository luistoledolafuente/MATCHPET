package com.matchpet.backend_user;

// --- Imports de JUnit y Mockito ---
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional; // ¡Importante para tests de 'create'!

// --- Imports de tu proyecto ---
import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.auth.LoginRequest;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.auth.RefreshTokenRequest;
import com.matchpet.backend_user.dto.refugio.RegisterRefugioRequest; // <-- ¡NUEVO IMPORT!
import com.fasterxml.jackson.databind.ObjectMapper;

// --- Imports estáticos (para hacer el código más limpio) ---
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // <-- ¡MUY RECOMENDADO! Hace rollback de la BD después de cada test
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * --- HU-01 / HU-02 / HU-03 ---
     * Prueba el flujo completo de Registro, Login y Perfil.
     */
    @Test
    public void testFullAuthenticationFlow() throws Exception {
        // ... (Tu test existente se queda igual)
        // --- 0. Preparación ---
        String uniqueEmail = "test-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "password123";
        String nombre = "Test User Flow";

        // --- 1. REGISTRO ---
        RegisterAdoptanteRequest registerRequest = new RegisterAdoptanteRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);
        registerRequest.setNombreCompleto(nombre);
        registerRequest.setTelefono("999888777");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        // --- 2. LOGIN ---
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String accessToken = authResponse.getAccessToken();

        // --- 3. ENDPOINT PROTEGIDO ---
        // 3a. Sin token (debe fallar)
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());

        // 3b. Con token (debe pasar)
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.nombreCompleto").value(nombre));
    }

    /**
     * --- HU-04 ---
     * Prueba el flujo correcto del Refresh Token.
     */
    @Test
    public void testRefreshTokenFlow_Success() throws Exception {
        // ... (Tu test existente se queda igual)
        // --- 1. SETUP ---
        String uniqueEmail = "test-refresh-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "password123";

        // Registrar usuario
        RegisterAdoptanteRequest registerRequest = new RegisterAdoptanteRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword(password);
        registerRequest.setNombreCompleto("Test Refresh");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Loguear usuario
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword(password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(jsonResponse, AuthResponse.class);
        String refreshToken = authResponse.getRefreshToken();
        String oldAccessToken = authResponse.getAccessToken();

        // --- FIX: Esperar un poco para asegurar que el nuevo token cambie ---
        Thread.sleep(100);

        // --- 2. REFRESH ---
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andReturn();

        // --- 3. Verificar que el nuevo access token sea diferente ---
        String refreshJsonResponse = refreshResult.getResponse().getContentAsString();
        AuthResponse refreshAuthResponse = objectMapper.readValue(refreshJsonResponse, AuthResponse.class);
        String newAccessToken = refreshAuthResponse.getAccessToken();

        assertNotNull(newAccessToken);
        assertNotEquals(oldAccessToken, newAccessToken);
    }

    /**
     * --- HU-04 (camino fallido) ---
     * Prueba cuando el Refresh Token es inválido.
     */
    @Test
    public void testRefreshTokenFlow_Fail_BadToken() throws Exception {
        // ... (Tu test existente se queda igual)
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("un.token.falso.y.malo");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }


    // --- ¡NUEVO TEST PARA H-5! ---

    /**
     * --- H-5 (Create) ---
     * Prueba el flujo de Registro de un nuevo Refugio.
     */
    @Test
    public void testRegisterRefugioFlow_Success() throws Exception {

        // --- 1. Preparación ---
        // Usar emails únicos para que el test se pueda correr varias veces
        String loginEmail = "refugio-login-" + System.currentTimeMillis() + "@matchpet.com";
        String refugioEmail = "refugio-public-" + System.currentTimeMillis() + "@matchpet.com";
        String password = "passwordRefugio123";
        String nombreRefugio = "Refugio Patitas Felices Test";
        String personaContacto = "Ana Test";

        // --- 2. Crear el Request DTO ---
        RegisterRefugioRequest request = RegisterRefugioRequest.builder()
                .emailLogin(loginEmail)
                .password(password)
                .nombreRefugio(nombreRefugio)
                .direccion("Av. Siempre Viva 123")
                .ciudad("Springfield")
                .emailRefugio(refugioEmail)
                .personaContacto(personaContacto)
                .telefonoContacto("987654321")
                .build();

        // --- 3. Ejecutar y Verificar ---
        mockMvc.perform(post("/api/auth/register-refugio") // <-- El nuevo endpoint
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // <-- El nuevo DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // Verifica que devuelve un token
                .andExpect(jsonPath("$.refreshToken").exists()); // Verifica que devuelve un refresh token
    }
}
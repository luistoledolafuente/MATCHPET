package com.matchpet.backend_user;

// --- Imports de Spring y JUnit ---
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

// --- Imports de JSON y Hamcrest (para asserts) ---
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.*;

// --- Imports de tus DTOs ---
import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.auth.LoginRequest;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;

// --- Imports de tus Modelos y Repositorios ---
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.model.lookup.*;
import com.matchpet.backend_user.repository.*;

// --- Imports de Java ---
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// --- Imports estáticos de MockMvc (para código limpio) ---
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // <-- ¡CRÍTICO! Revierte la BD después de cada test
public class AnimalIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    // --- Repositorios para crear datos de prueba ---
    @Autowired private UserRepository userRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private RefugioRepository refugioRepository;
    @Autowired private AnimalRepository animalRepository;
    @Autowired private EspecieRepository especieRepository;
    @Autowired private RazaRepository razaRepository;
    @Autowired private GeneroRepository generoRepository;
    @Autowired private TamanoRepository tamanoRepository;
    @Autowired private NivelEnergiaRepository nivelEnergiaRepository;
    @Autowired private EstadoAdopcionRepository estadoAdopcionRepository;
    @Autowired private TemperamentoRepository temperamentoRepository;

    // --- Variables que usaremos en todos los tests ---
    private String accessToken; // Token del refugio
    private Refugio testRefugio;

    // IDs de los datos de prueba que crearemos
    private Integer razaId;
    private Integer generoId;
    private Integer estadoAdopcionId;
    private Integer tamanoId;
    private Integer nivelEnergiaId;
    private Integer temperamentoId1;
    private Integer temperamentoId2;

    /**
     * Este método se ejecuta ANTES de CADA test.
     * Crea un entorno limpio:
     * 1. Crea los datos de consulta (Raza, Genero, etc.)
     * 2. Crea un usuario de tipo "Refugio".
     * 3. Inicia sesión como ese Refugio para obtener un accessToken.
     */
    @BeforeEach
    void setUp() throws Exception {

        // 1. Crear datos de Consulta (Lookups)
        Especie perro = new Especie();
        perro.setNombreEspecie("Perro (Test)");
        perro = especieRepository.save(perro);

        Raza raza = new Raza();
        raza.setNombreRaza("Raza (Test)");
        raza.setEspecie(perro);
        raza = razaRepository.save(raza);
        razaId = raza.getId();

        Genero genero = new Genero();
        genero.setNombre("Macho (Test)");
        generoId = generoRepository.save(genero).getId();

        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setNombre("Disponible (Test)");
        estadoAdopcionId = estadoAdopcionRepository.save(estado).getId();

        Tamano tamano = new Tamano();
        tamano.setNombre("Mediano (Test)");
        tamanoId = tamanoRepository.save(tamano).getId();

        NivelEnergia nivel = new NivelEnergia();
        nivel.setNombre("Alto (Test)");
        nivelEnergiaId = nivel.getId();

        Temperamento temp1 = new Temperamento();
        temp1.setNombreTemperamento("Juguetón (Test)");
        temperamentoId1 = temperamentoRepository.save(temp1).getId();

        Temperamento temp2 = new Temperamento();
        temp2.setNombreTemperamento("Dormilón (Test)");
        temperamentoId2 = temperamentoRepository.save(temp2).getId();

        // 2. Crear el usuario Refugio
        RolModel refugioRole = rolRepository.findByNombreRol("Refugio")
                .orElseGet(() -> rolRepository.save(new RolModel(null, "Refugio")));

        Refugio refugio = new Refugio();
        refugio.setNombre("Refugio de Prueba para Animales");
        refugio.setDireccion("Direccion Test");
        refugio.setCiudad("Ciudad Test");
        refugio.setEmail("email.refugio.test@matchpet.com");
        refugio.setPersonaContacto("Contacto Test");
        refugio.setTelefono("123456789");

        UserModel user = new UserModel();
        user.setEmail("user.refugio.animaltest@matchpet.com");
        user.setHashContrasena(passwordEncoder.encode("password123"));
        user.setNombreCompleto("Usuario Refugio Test");
        user.setRoles(new HashSet<>(Set.of(refugioRole)));
        user.setEstaActivo(true);
        user.setRefugio(refugio);
        refugio.setUser(user);

        userRepository.save(user);
        testRefugio = user.getRefugio(); // Guardamos la referencia al refugio creado

        // 3. Iniciar sesión para obtener el token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user.refugio.animaltest@matchpet.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponse.class);
        accessToken = authResponse.getAccessToken();
    }

    /**
     * --- Test [C]REATE ---
     * Prueba: POST /api/animales
     */
    @Test
    void testCreateAnimal_Success() throws Exception {
        CreateAnimalRequest request = CreateAnimalRequest.builder()
                .nombre("Firulais Test")
                .razaId(razaId)
                .generoId(generoId)
                .estadoAdopcionId(estadoAdopcionId)
                .tamanoId(tamanoId)
                .nivelEnergiaId(nivelEnergiaId)
                .temperamentosIds(Set.of(temperamentoId1, temperamentoId2))
                .fotosUrls(List.of("http://foto.com/1.jpg"))
                .fotoPrincipalIndex(0)
                .descripcionPersonalidad("Un perro de prueba")
                .estaVacunado(true)
                .estaEsterilizado(false)
                .fechaIngresoRefugio(Date.valueOf("2025-01-01"))
                .build();

        mockMvc.perform(post("/api/animales")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Firulais Test"))
                .andExpect(jsonPath("$.raza.nombreRaza").value("Raza (Test)"))
                .andExpect(jsonPath("$.fotos", hasSize(1)))
                .andExpect(jsonPath("$.temperamentos", hasSize(2)));
    }

    /**
     * --- Test [R]EAD ---
     * Prueba: GET /api/animales/mis-animales
     */
    @Test
    void testGetMisAnimales_Success() throws Exception {
        // Setup: Crear un animal para que la lista no esté vacía
        crearAnimalDePrueba("Buddy");

        mockMvc.perform(get("/api/animales/mis-animales")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Espera una lista con 1 elemento
                .andExpect(jsonPath("$[0].nombre").value("Buddy"));
    }

    /**
     * --- Test [U]PDATE ---
     * Prueba: PUT /api/animales/{id}
     */
    @Test
    void testUpdateAnimal_Success() throws Exception {
        // Setup: Crear el animal a actualizar
        Animal animalGuardado = crearAnimalDePrueba("Max");

        UpdateAnimalRequest request = UpdateAnimalRequest.builder()
                .nombre("Max Actualizado") // <-- El cambio
                .razaId(razaId)
                .generoId(generoId)
                .estadoAdopcionId(estadoAdopcionId)
                .tamanoId(tamanoId)
                .nivelEnergiaId(nivelEnergiaId)
                .temperamentosIds(Set.of(temperamentoId1)) // Mantenemos el mismo
                .fotosUrls(List.of("http://foto.com/nueva.jpg")) // Nueva lista de fotos
                .fotoPrincipalIndex(0)
                .descripcionPersonalidad("Descripción actualizada")
                .estaVacunado(true)
                .estaEsterilizado(true)
                .build();

        mockMvc.perform(put("/api/animales/" + animalGuardado.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Max Actualizado"))
                .andExpect(jsonPath("$.estaEsterilizado").value(true))
                .andExpect(jsonPath("$.fotos", hasSize(1)))
                .andExpect(jsonPath("$.fotos[0].urlFoto").value("http://foto.com/nueva.jpg"));
    }

    /**
     * --- Test [D]ELETE ---
     * Prueba: DELETE /api/animales/{id}
     */
    @Test
    void testDeleteAnimal_Success() throws Exception {
        // Setup: Crear el animal a eliminar
        Animal animalGuardado = crearAnimalDePrueba("Borrable");

        // Ejecutar DELETE
        mockMvc.perform(delete("/api/animales/" + animalGuardado.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Animal eliminado exitosamente"));

        // Verificar que ya no está en la lista
        mockMvc.perform(get("/api/animales/mis-animales")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // Espera una lista vacía
    }


    /**
     * Método helper para crear un animal simple para los tests
     */
    private Animal crearAnimalDePrueba(String nombre) {
        Animal animal = new Animal();
        animal.setNombre(nombre);
        animal.setRefugio(testRefugio);
        animal.setRaza(razaRepository.findById(razaId).get());
        animal.setGenero(generoRepository.findById(generoId).get());
        animal.setEstadoAdopcion(estadoAdopcionRepository.findById(estadoAdopcionId).get());
        animal.setTemperamentos(Set.of(temperamentoRepository.findById(temperamentoId1).get()));
        return animalRepository.save(animal);
    }
}
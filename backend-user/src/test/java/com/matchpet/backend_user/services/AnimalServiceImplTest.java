package com.matchpet.backend_user.services;


import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.model.*;
import com.matchpet.backend_user.model.lookup.*;
import com.matchpet.backend_user.repository.*;
import com.matchpet.backend_user.service.Imp.AnimalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AnimalServiceImplTest {

    @InjectMocks
    private AnimalServiceImpl animalService;

    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RazaRepository razaRepository;
    @Mock
    private GeneroRepository generoRepository;
    @Mock
    private TamanoRepository tamanoRepository;
    @Mock
    private NivelEnergiaRepository nivelEnergiaRepository;
    @Mock
    private EstadoAdopcionRepository estadoAdopcionRepository;
    @Mock
    private TemperamentoRepository temperamentoRepository;
    @Mock
    private AnimalFotoRepository animalFotoRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAnimal_OK() {
        // ----- GIVEN -----
        CreateAnimalRequest req = new CreateAnimalRequest();
        req.setNombre("Luna");
        req.setFechaNacimientoAprox(LocalDate.of(2020,1,1));
        req.setFechaIngresoRefugio(LocalDate.now());
        req.setEstaVacunado(true);
        req.setCompatibleNiños(true);
        req.setCompatibleOtrasMascotas(false);
        req.setDescripcionPersonalidad("Cariñosa");
        req.setHistorialMedico("Vacunas ok");

        req.setRazaId(1);
        req.setGeneroId(1);
        req.setTemperamentosIds(Set.of(1,2));
        req.setEstadoAdopcionId(1);
        req.setFotoPrincipalIndex(0);
        req.setFotosUrls(List.of("fotoA"));

        req.setTamanoId(1);
        req.setNivelEnergiaId(1);

        // user email
        String userEmail = "refugio@mail.com";

        // usuario + refugio
        Refugio refugio = new Refugio();
        refugio.setId(10);
        refugio.setNombre("Refugio Sol");
        refugio.setCiudad("Cusco");

        UserModel user = new UserModel();
        user.setEmail(userEmail);
        user.setRefugio(refugio);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        // entidades lookup
        Raza raza = new Raza();
        raza.setId(1);
        Especie especie = new Especie();
        especie.setNombreEspecie("Perro");
        raza.setEspecie(especie);
        raza.setNombreRaza("Pitbull");

        when(razaRepository.findById(1)).thenReturn(Optional.of(raza));

        Genero genero = new Genero();
        genero.setId(1);
        genero.setNombre("Hembra");
        when(generoRepository.findById(1)).thenReturn(Optional.of(genero));

        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setId(1);
        estado.setNombre("Disponible");
        when(estadoAdopcionRepository.findById(1)).thenReturn(Optional.of(estado));

        Tamano tamano = new Tamano();
        tamano.setId(1);
        tamano.setNombre("Mediano");
        when(tamanoRepository.findById(1)).thenReturn(Optional.of(tamano));

        NivelEnergia ne = new NivelEnergia();
        ne.setId(1);
        ne.setNombre("Moderado");
        when(nivelEnergiaRepository.findById(1)).thenReturn(Optional.of(ne));

        Temperamento t1 = new Temperamento();
        t1.setId(1);
        t1.setNombreTemperamento("Juguetón");

        Temperamento t2 = new Temperamento();
        t2.setId(2);
        t2.setNombreTemperamento("Tranquilo");

        when(temperamentoRepository.findAllById(Set.of(1,2))).thenReturn(List.of(t1,t2));

        // simulación de repositorio save
        when(animalRepository.save(any(Animal.class)))
                .thenAnswer(invoc -> {
                    Animal a = invoc.getArgument(0);
                    a.setId(999);
                    return a;
                });

        // ---- WHEN ----
        var result = animalService.createAnimal(req, userEmail);

        // ---- THEN ----
        assertNotNull(result);
        assertEquals("Luna", result.getNombre());
        assertEquals("Perro", result.getEspecie());
        assertEquals("Pitbull", result.getRaza());
        assertEquals("Hembra", result.getGenero());
        assertEquals("Mediano", result.getTamano());
        assertEquals("Moderado", result.getNivelEnergia());
        assertEquals("Disponible", result.getEstadoAdopcion());
        assertEquals("Refugio Sol", result.getRefugioNombre());

        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    void getAnimalesByRefugio_OK() {
        // ---- GIVEN ----
        String email = "admin@refugio.com";

        Refugio refugio = new Refugio();
        refugio.setId(1);
        refugio.setNombre("Refugio Patitas");

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setRefugio(refugio);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // ==== DATA NECESARIA PARA NO NPE ====

        Genero genero = new Genero();
        genero.setId(1);
        genero.setNombre("Hembra");

        Especie especie = new Especie();
        especie.setNombreEspecie("Perro");

        Raza raza = new Raza();
        raza.setId(1);
        raza.setNombreRaza("Mestizo");
        raza.setEspecie(especie);

        Tamano tamano = new Tamano();
        tamano.setId(1);
        tamano.setNombre("Mediano");

        NivelEnergia ne = new NivelEnergia();
        ne.setId(1);
        ne.setNombre("Moderado");

        EstadoAdopcion estado = new EstadoAdopcion();
        estado.setId(1);
        estado.setNombre("Disponible");

        Temperamento t1 = new Temperamento();
        t1.setId(1);
        t1.setNombreTemperamento("Juguetón");

        Set<Temperamento> temps = Set.of(t1);

        // ==== ANIMALES SIMULADOS ====

        Animal a1 = new Animal();
        a1.setId(10);
        a1.setNombre("Luna");
        a1.setRefugio(refugio);
        a1.setGenero(genero);
        a1.setRaza(raza);
        a1.setTamano(tamano);
        a1.setNivelEnergia(ne);
        a1.setEstadoAdopcion(estado);
        a1.setTemperamentos(temps);

        Animal a2 = new Animal();
        a2.setId(11);
        a2.setNombre("Max");
        a2.setRefugio(refugio);
        a2.setGenero(genero);
        a2.setRaza(raza);
        a2.setTamano(tamano);
        a2.setNivelEnergia(ne);
        a2.setEstadoAdopcion(estado);
        a2.setTemperamentos(temps);

        when(animalRepository.findByRefugio(refugio))
                .thenReturn(List.of(a2, a1));

        // ---- WHEN ----
        var result = animalService.getAnimalesByRefugio(email);

        // ---- THEN ----
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Max", result.get(0).getNombre());
        assertEquals("Luna", result.get(1).getNombre());

        verify(userRepository, times(1)).findByEmail(email);
        verify(animalRepository, times(1)).findByRefugio(refugio);
    }


    @Test
    void createAnimal_RazaNotFound() {
        CreateAnimalRequest req = new CreateAnimalRequest();
        req.setRazaId(99);

        when(razaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            animalService.createAnimal(req, "test@mail.com");
        });
    }

    @Test
    void getAnimalesByRefugio_UserNotFound() {
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                animalService.getAnimalesByRefugio("notfound@mail.com"));
    }


}

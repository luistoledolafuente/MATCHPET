package com.matchpet.backend_user.web;

import com.matchpet.backend_user.controller.AnimalController;
/*import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
public class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;

    @Test
    void createAnimal_OK() throws Exception {

        Mockito.when(animalService.createAnimal(Mockito.any(), Mockito.any()))
                .thenReturn(new AnimalDTO());

        String json = """
        {
            "fechaNacimientoAprox": "2020-05-10",
            "temperamentosIds": [1,2],
            "descripcionPersonalidad": "Juguetón y sociable",
            "historialMedico": "Vacunas al día",
            "nombre": "Max",
            "tamanoId": 1,
            "razaId": 1,
            "generoId": 1,
            "fechaIngresoRefugio": "2023-11-30",
            "estadoAdopcionId": 1,
            "nivelEnergiaId": 2,
            "fotosUrls": ["http://image.com/perrito.jpg"]
        }
        """;

        mockMvc.perform(post("/api/animales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}*/

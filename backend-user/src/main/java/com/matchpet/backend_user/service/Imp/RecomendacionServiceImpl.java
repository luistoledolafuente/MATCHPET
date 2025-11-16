package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.AnimalRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.RecomendacionService;

// Imports de la librería de Gemini que elegiste
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomendacionServiceImpl implements RecomendacionService {

    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final Gson gson = new Gson();

    @Value("${gemini.model.name}")
    private String modelName;

    private Client client;

    @PostConstruct
    public void init() {
        // Esto lee la variable de entorno GEMINI_API_KEY
        // que configuraste en tu IDE
        this.client = new Client();
    }

    @Override
    public List<AnimalDTO> getRecomendaciones(String adoptanteEmail) {

        // 1. Obtener datos
        UserModel user = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // (Usamos .getAdoptante() como en tu UserModel.java)
        PerfilAdoptante perfil = user.getAdoptante();

        // (Usamos el método que añadiste al AnimalRepository)
        List<Animal> mascotasDisponibles = animalRepository.findByEstadoAdopcionId(1); // 1 = Disponible

        // 2. Convertir a JSON
        // (Esto usa el AnimalDTO.java corregido)
        String perfilJson = gson.toJson(perfil);
        String mascotasJson = mascotasDisponibles.stream()
                .map(AnimalDTO::new)
                .collect(Collectors.toList()).toString();

        String prompt = construirPrompt(perfilJson, mascotasJson);

        try {
            // 3. Llamar a la API de Gemini
            GenerateContentResponse response = this.client.models.generateContent(
                    modelName,
                    prompt, // Pasamos el prompt como un String
                    null    // Para GenerationConfig
            );

            // 4. Obtener la respuesta
            String respuestaTexto = response.text();

            // 5. Parsear la respuesta
            List<Integer> idsRecomendados = gson.fromJson(respuestaTexto,
                    new TypeToken<List<Integer>>(){}.getType());

            // 6. Devolver los animales de la BD
            return animalRepository.findAllById(idsRecomendados).stream()
                    .map(AnimalDTO::new)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a la API de Gemini: " + e.getMessage());
        }
    }

    /**
     * Método helper para construir el prompt.
     */
    private String construirPrompt(String perfilAdoptante, String listaMascotas) {
        return "Eres 'Match IA', un asistente experto en adopción de mascotas para la app MatchPet. " +
                "Tu trabajo es analizar el perfil de un adoptante y una lista de mascotas disponibles, y encontrar las 3 mejores coincidencias. " +
                "Considera el estilo de vida, el tamaño de la vivienda (basado en la ciudad/dirección), el nivel de energía y el temperamento. " +
                "\n\n" +
                "--- PERFIL DEL ADOPTANTE ---" +
                "\n" +
                perfilAdoptante +
                "\n\n" +
                "--- MASCOTAS DISPONIBLES ---" +
                "\n" +
                listaMascotas +
                "\n\n" +
                "--- TAREA ---" +
                "Devuelve SOLAMENTE un array JSON con los IDs (ej: `animal_id`) de las 3 mejores mascotas para este adoptante. " +
                "No incluyas explicaciones, solo el array JSON. Ejemplo de respuesta: [1, 5, 12]";
    }
}
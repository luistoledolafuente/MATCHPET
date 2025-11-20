package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.AnimalRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.RecomendacionService;

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

    @Value("${google.api.key}")
    private String apiKey; // Mantenemos la inyección de la clave por si acaso

    private Client client;

    @PostConstruct
    public void init() {
        // CORRECCIÓN 1: Usamos el constructor sin argumentos para resolver el error de compilación
        // y confiar en que el SDK de Gemini encuentre la clave de entorno/propiedades.
        this.client = new Client();
    }

    @Override
    public List<AnimalDTO> getRecomendaciones(String adoptanteEmail) {

        UserModel user = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilAdoptante perfil = user.getAdoptante();

        List<Animal> mascotasDisponibles = animalRepository.findByEstadoAdopcionId(1);

        List<AnimalDTO> mascotasDTOs = mascotasDisponibles.stream()
                .map(this::convertAnimalToDTO)
                .collect(Collectors.toList());

        String perfilJson = gson.toJson(perfil);
        String mascotasJson = gson.toJson(mascotasDTOs);

        String prompt = construirPrompt(perfilJson, mascotasJson);

        try {
            GenerateContentResponse response = this.client.models.generateContent(
                    modelName,
                    prompt,
                    null
            );

            String respuestaTexto = response.text().trim();

            List<Integer> idsRecomendados = gson.fromJson(respuestaTexto,
                    new TypeToken<List<Integer>>(){}.getType());

            // CORRECCIÓN 2: Llamamos findAllById con la lista de Integer directamente,
            // ya que el ID de Animal es Integer.
            return animalRepository.findAllById(idsRecomendados).stream()
                    .map(this::convertAnimalToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a la API de Gemini: " + e.getMessage());
        }
    }

    // Nota: Este método DEBE existir en tu AnimalServiceImpl
    // Lo replicamos aquí para la compilación de este servicio.
    private AnimalDTO convertAnimalToDTO(Animal animal) {
        return AnimalDTO.builder()
                .animal_id(animal.getId())
                .nombre(animal.getNombre())
                // ** DEBES ASEGURARTE QUE TODOS LOS CAMPOS DE ANIMALDTO ESTÉN AQUÍ **
                .build();
    }

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
package com.matchpet.backend_user.service.Imp;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.AnimalFoto;
import com.matchpet.backend_user.model.PerfilAdoptante;
import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.Temperamento;
import com.matchpet.backend_user.model.UserModel;
import com.matchpet.backend_user.repository.AnimalRepository;
import com.matchpet.backend_user.repository.UserRepository;
import com.matchpet.backend_user.service.RecomendacionService;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

@Service
@RequiredArgsConstructor
public class RecomendacionServiceImpl implements RecomendacionService {

    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final Gson gson = buildCustomGson();

    @Value("${gemini.model.name}")
    private String modelName;

    @Value("${google.api.key}")
    private String apiKey;

    private Client client;

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(FORMATTER.format(value));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString(), FORMATTER);
        }
    }

    private static class CycleBreakerExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            if (f.getDeclaringClass() == UserModel.class && f.getName().equals("adoptante")) {
                return true;
            }
            if (f.getDeclaringClass() == PerfilAdoptante.class && f.getName().equals("user")) {
                return true;
            }
            if (f.getDeclaringClass() == Animal.class && f.getName().equals("refugio")) {
                return true;
            }
            if (f.getDeclaringClass() == UserModel.class && (f.getName().equals("roles") || f.getName().equals("donaciones"))) {
                return true;
            }
            if (f.getDeclaringClass() == Refugio.class && f.getName().equals("animales")) {
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private Gson buildCustomGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setExclusionStrategies(new CycleBreakerExclusionStrategy())
                .create();
    }

    @PostConstruct
    public void init() {
        this.client = new Client();
    }

    @Override
    public List<AnimalDTO> getRecomendaciones(String adoptanteEmail) {
        UserModel user = userRepository.findByEmail(adoptanteEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilAdoptante perfil = user.getAdoptante();

        if (perfil == null) {
            throw new RuntimeException("El perfil de adoptante no está completo o no existe.");
        }

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

            String jsonLimpio = limpiarRespuestaGemini(respuestaTexto);

            List<Integer> idsRecomendados = gson.fromJson(jsonLimpio,
                    new TypeToken<List<Integer>>(){}.getType());

            return animalRepository.findAllById(idsRecomendados).stream()
                    .map(this::convertAnimalToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error en el servicio de recomendación (Gemini): " + e.getMessage());
        }
    }

    private String limpiarRespuestaGemini(String rawResponse) {
        Pattern pattern = Pattern.compile("```json\\s*\\[([^\\]]*)\\]\\s*```|\\s*\\[([^\\]]*)\\]\\s*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(rawResponse);

        if (matcher.find()) {
            String content = matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(2).trim();
            return "[" + content + "]";
        }

        int start = rawResponse.indexOf('[');
        int end = rawResponse.lastIndexOf(']');

        if (start != -1 && end != -1 && end > start) {
            return rawResponse.substring(start, end + 1);
        }

        return rawResponse;
    }

    private AnimalDTO convertAnimalToDTO(Animal animal) {
        return AnimalDTO.builder()
                .animal_id(animal.getId())
                .nombre(animal.getNombre())
                .fechaNacimientoAprox(animal.getFechaNacimientoAprox())
                .descripcionPersonalidad(animal.getDescripcionPersonalidad())
                .compatibleNiños(animal.isCompatibleNiños())
                .compatibleOtrasMascotas(animal.isCompatibleOtrasMascotas())
                .estaVacunado(animal.isEstaVacunado())
                .estaEsterilizado(animal.isEstaEsterilizado())
                .historialMedico(animal.getHistorialMedico())
                .fechaIngresoRefugio(animal.getFechaIngresoRefugio())
                .raza(animal.getRaza().getNombreRaza())
                .especie(animal.getRaza().getEspecie().getNombreEspecie())
                .genero(animal.getGenero().getNombre())
                .tamano(animal.getTamano().getNombre())
                .nivelEnergia(animal.getNivelEnergia().getNombre())
                .estadoAdopcion(animal.getEstadoAdopcion().getNombre())
                .refugioNombre(animal.getRefugio().getNombre())
                .refugioCiudad(animal.getRefugio().getCiudad())
                .temperamentos(animal.getTemperamentos().stream()
                        .map(Temperamento::getNombreTemperamento)
                        .collect(Collectors.toList()))
                .fotos(animal.getFotos().stream()
                        .map(AnimalFoto::getUrlFoto)
                        .collect(Collectors.toList()))
                .build();
    }

    private String construirPrompt(String perfilAdoptanteJson, String listaMascotasJson) {
        return "Eres 'Match IA', un asistente experto en adopción de mascotas para la app MatchPet. " +
                "Tu tarea es analizar el perfil del adoptante y la lista de mascotas, y devolver SOLAMENTE una lista JSON de los IDs de las 3 mejores coincidencias." +
                "\n\n" +
                "--- PERFIL DEL ADOPTANTE ---\n" +
                perfilAdoptanteJson +
                "\n\n" +
                "--- MASCOTAS DISPONIBLES ---\n" +
                listaMascotasJson +
                "\n\n" +
                "--- TAREA DE SALIDA (FORMATO REQUERIDO) ---" +
                "\n" +
                "Devuelve la respuesta dentro de un bloque JSON. NO incluyas ninguna explicación, texto adicional o comentarios fuera del bloque JSON." +
                "\n" +
                "JSON DE SALIDA (SOLO el array JSON de IDs de enteros):";
    }
}

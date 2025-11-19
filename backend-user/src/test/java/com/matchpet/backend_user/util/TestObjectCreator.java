//package com.matchpet.backend_user.util;
//
//import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
//import com.matchpet.backend_user.model.Animal;
//import com.matchpet.backend_user.model.Refugio;
//import com.matchpet.backend_user.model.Raza;
//import com.matchpet.backend_user.model.lookup.*;
//
//import java.util.HashSet;
//import java.util.List;
//
///**
// * Equivalente al TObjectCreator del profesor.
// * Usado para crear entidades y DTOs falsos para las pruebas.
// */
//public class TestObjectCreator {
//
//    /**
//     * Simula un animal completo, como si viniera de la BD (para Mocks).
//     * @return Animal completo
//     */
//    public static Animal createMockAnimal() {
//        Animal animal = new Animal();
//        animal.setIdAnimal(1L);
//        animal.setNombre("Fido Mockeado");
//        animal.setCompatibleNiños(true);
//        animal.setCompatibleOtrasMascotas(true);
//        animal.setEstaEsterilizado(true);
//        animal.setEstaVacunado(true);
//
//        // Asignamos objetos (aunque sean vacíos) para evitar NullPointerException en el mapeo
//        animal.setRaza(new Raza(1L, "Mestizo (Perro)", new Especie(1L, "Perro")));
//        animal.setRefugio(new Refugio(1L, "Refugio Patitas Felices", "Lima", null, null, null, null));
//        animal.setGenero(new Genero(1, "Macho"));
//        animal.setTamano(new Tamano(2, "Mediano"));
//        animal.setNivelEnergia(new NivelEnergia(3, "Alto"));
//        animal.setEstadoAdopcion(new EstadoAdopcion(1, "Disponible"));
//        animal.setFotos(new HashSet<>());
//        animal.setTemperamentos(new HashSet<>());
//
//        return animal;
//    }
//
//    /**
//     * Simula un DTO para la prueba de creación.
//     * @return CreateAnimalRequest
//     */
//    public static CreateAnimalRequest createMockAnimalRequest() {
//        return CreateAnimalRequest.builder()
//                .nombre("Buddy")
//                .razaId(1) // Labrador
//                .refugioId(1) // Patitas Felices
//                .generoId(1) // Macho
//                .tamanoId(2) // Mediano
//                .nivelEnergiaId(3) // Alto
//                .estadoAdopcionId(1) // Disponible
//                .compatibleNiños(true)
//                .compatibleOtrasMascotas(true)
//                .estaEsterilizado(false)
//                .estaVacunado(true)
//                .temperamentos(new HashSet<>(List.of(1, 4))) // Juguetón, Energético
//                .fotos(List.of("http://foto.com/buddy1.jpg"))
//                .build();
//    }
//
//    /**
//     * Simula el 'Animal' que el repositorio debería guardar (sin ID).
//     * @return Animal sin ID
//     */
//    public static Animal newAnimalFromRequest() {
//        Animal animal = new Animal();
//        animal.setNombre("Buddy");
//        animal.setCompatibleNiños(true);
//        // ... (mapear el resto de campos del DTO)
//        return animal;
//    }
//
//    /**
//     * Simula el 'Animal' que el repositorio devuelve (con ID).
//     * @return Animal con ID
//     */
//    public static Animal createdAnimalFromRequest() {
//        Animal animal = newAnimalFromRequest();
//        animal.setIdAnimal(100L); // ID de prueba
//        // ... (mapear y asignar el resto de entidades lookup)
//        animal.setRaza(new Raza(1L, "Mestizo (Perro)", new Especie(1L, "Perro")));
//        animal.setRefugio(new Refugio(1L, "Refugio Patitas Felices", "Lima", null, null, null, null));
//        animal.setGenero(new Genero(1, "Macho"));
//        animal.setTamano(new Tamano(2, "Mediano"));
//        animal.setNivelEnergia(new NivelEnergia(3, "Alto"));
//        animal.setEstadoAdopcion(new EstadoAdopcion(1, "Disponible"));
//        animal.setFotos(new HashSet<>());
//        animal.setTemperamentos(new HashSet<>());
//        return animal;
//    }
//}
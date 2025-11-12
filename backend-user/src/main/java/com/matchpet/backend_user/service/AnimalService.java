package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.model.Animal;

public interface AnimalService {

    /**
     * Crea un nuevo animal y lo asocia al refugio del usuario autenticado.
     *
     * @param request El DTO con la información del nuevo animal.
     * @param userEmail El email del usuario (Refugio) que está creando el animal.
     * @return La entidad del Animal creado.
     */
    Animal createAnimal(CreateAnimalRequest request, String userEmail);

    // Aquí es donde irán los otros métodos de la HU-06:
    // List<Animal> getAnimalesByRefugio(String userEmail);
    // Animal updateAnimal(Integer animalId, UpdateAnimalRequest request);
    // void deleteAnimal(Integer animalId);
}
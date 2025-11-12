package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;

import java.util.List;

public interface AnimalService {

    /**
     * Crea un nuevo animal y lo asocia al refugio del usuario autenticado.
     */
    AnimalDTO createAnimal(CreateAnimalRequest request, String userEmail);

    /**
     * Obtiene la lista de todos los animales pertenecientes a un refugio.
     */
    List<AnimalDTO> getAnimalesByRefugio(String userEmail);

    /**
     * Actualiza un animal existente, verificando que pertenezca al refugio.
     */
    AnimalDTO updateAnimal(Integer animalId, UpdateAnimalRequest request, String userEmail);

    /**
     * ¡NUEVO MÉTODO PARA DELETE! [HU-06]
     * Elimina un animal, verificando que pertenezca al refugio.
     *
     * @param animalId El ID del animal a eliminar.
     * @param userEmail El email del usuario (Refugio) que realiza la acción.
     */
    void deleteAnimal(Integer animalId, String userEmail);
}
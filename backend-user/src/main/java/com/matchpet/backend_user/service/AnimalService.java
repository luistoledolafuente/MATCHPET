package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.animal.CreateAnimalRequest;
import com.matchpet.backend_user.dto.animal.UpdateAnimalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz de servicio para la gestión de Animales.
 * Define las operaciones de negocio para los animales,
 * asegurando la lógica de permisos por refugio.
 */
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
     * Elimina un animal, verificando que pertenezca al refugio.
     */
    void deleteAnimal(Integer animalId, String userEmail);

    /**
     * Obtiene un animal por su ID.
     */
    AnimalDTO getAnimalById(Integer id);

    /**
     * Obtiene una lista paginada de todos los animales.
     * (¡AÑADIDO! Faltaba en la interfaz)
     */
    Page<AnimalDTO> getAnimalesPaginados(Pageable pageable);
}
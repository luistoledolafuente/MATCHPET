package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.auth.AuthResponse;
import com.matchpet.backend_user.dto.animal.AnimalDTO;
import com.matchpet.backend_user.dto.adoptante.RegisterAdoptanteRequest;
import com.matchpet.backend_user.dto.adoptante.UpdateAdoptanteRequest;
import com.matchpet.backend_user.dto.user.UserProfileResponse;
import java.util.List;

public interface AdoptanteService {

    /**
     * Registra un nuevo Adoptante en el sistema.
     * (Lógica movida desde AuthService)
     */
    AuthResponse registerAdoptante(RegisterAdoptanteRequest request);

    /**
     * Actualiza el perfil de un adoptante existente. [HU-05]
     */
    UserProfileResponse updateAdoptante(Integer usuarioId, UpdateAdoptanteRequest request);
    void addFavoriteAnimal(String userEmail, Integer animalId);
    void removeFavoriteAnimal(String userEmail, Integer animalId);
    List<AnimalDTO> getFavoriteAnimals(String userEmail);
}
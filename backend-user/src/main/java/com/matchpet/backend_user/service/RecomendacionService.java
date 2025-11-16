package com.matchpet.backend_user.service;
import com.matchpet.backend_user.dto.animal.AnimalDTO;
import java.util.List;

public interface RecomendacionService {
    List<AnimalDTO> getRecomendaciones(String adoptanteEmail);
}
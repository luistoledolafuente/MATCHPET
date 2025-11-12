package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.PerfilAdoptante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilAdoptanteRepository extends JpaRepository<PerfilAdoptante, Integer> {
    // El ID de PerfilAdoptante es el mismo que el de Usuario,
    // así que JpaRepository.findById() nos sirve perfectamente.
}
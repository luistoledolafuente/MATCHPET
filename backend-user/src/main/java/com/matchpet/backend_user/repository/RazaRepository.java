package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RazaRepository extends JpaRepository<Raza, Integer> {
    // Más adelante podríamos añadir: List<Raza> findByEspecie(Especie especie);
}
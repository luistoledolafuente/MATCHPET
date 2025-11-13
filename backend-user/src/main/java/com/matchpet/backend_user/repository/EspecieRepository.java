package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.lookup.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Integer> {
    // Spring Data JPA crea mágicamente el 'findById', 'findAll', etc.
}
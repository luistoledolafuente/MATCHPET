package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.AnimalFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalFotoRepository extends JpaRepository<AnimalFoto, Integer> {
}
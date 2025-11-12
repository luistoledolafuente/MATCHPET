package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Temperamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperamentoRepository extends JpaRepository<Temperamento, Integer> {
}
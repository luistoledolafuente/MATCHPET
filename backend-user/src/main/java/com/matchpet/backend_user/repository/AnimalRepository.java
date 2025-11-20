package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.Refugio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {

    List<Animal> findByRefugio(Refugio refugio);

    Page<Animal> findByRefugio(Refugio refugio, Pageable pageable);

    List<Animal> findByEstadoAdopcionId(Integer id);

}
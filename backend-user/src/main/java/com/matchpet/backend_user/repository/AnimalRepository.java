package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.Refugio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {

    /**
     * ¡Magia de Spring Data JPA!
     * Esto crea automáticamente una consulta para encontrar todos
     * los animales que pertenecen a un refugio específico.
     * Lo usaremos para el panel del refugio.
     */
    List<Animal> findByRefugio(Refugio refugio);
}
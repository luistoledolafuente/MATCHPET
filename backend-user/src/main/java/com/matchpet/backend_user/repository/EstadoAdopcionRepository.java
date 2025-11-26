// EstadoAdopcionRepository.java
package com.matchpet.backend_user.repository;
import com.matchpet.backend_user.model.lookup.EstadoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoAdopcionRepository extends JpaRepository<EstadoAdopcion, Integer> {
    Optional<EstadoAdopcion> findByNombre(String nombre);
}
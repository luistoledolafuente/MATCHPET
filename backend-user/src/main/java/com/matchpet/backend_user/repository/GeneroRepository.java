// GeneroRepository.java
package com.matchpet.backend_user.repository;
import com.matchpet.backend_user.model.lookup.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GeneroRepository extends JpaRepository<Genero, Integer> {}
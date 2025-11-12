// TamanoRepository.java
package com.matchpet.backend_user.repository;
import com.matchpet.backend_user.model.lookup.Tamano;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TamanoRepository extends JpaRepository<Tamano, Integer> {}
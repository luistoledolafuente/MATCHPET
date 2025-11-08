// EstadoPagoRepository.java
package com.matchpet.backend_user.repository;
import com.matchpet.backend_user.model.lookup.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstadoPagoRepository extends JpaRepository<EstadoPago, Integer> {}
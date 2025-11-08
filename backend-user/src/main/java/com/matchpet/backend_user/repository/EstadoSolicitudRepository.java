// EstadoSolicitudRepository.java
package com.matchpet.backend_user.repository;
import com.matchpet.backend_user.model.lookup.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Integer> {}
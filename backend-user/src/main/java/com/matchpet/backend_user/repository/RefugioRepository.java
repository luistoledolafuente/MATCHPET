package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Refugio;
import com.matchpet.backend_user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Integer> {
    Optional<Refugio> findByUser(UserModel user);
}

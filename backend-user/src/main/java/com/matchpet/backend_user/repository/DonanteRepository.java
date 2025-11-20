package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Donante;
import com.matchpet.backend_user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Integer> {

    Optional<Donante> findByEmail(String email);
    Optional<Donante> findByUser(UserModel user);
}
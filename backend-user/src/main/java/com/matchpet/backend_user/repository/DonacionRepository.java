package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Donacion;
import com.matchpet.backend_user.model.Donante; // <-- ¡NUEVO IMPORT!
import com.matchpet.backend_user.model.Refugio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonacionRepository extends JpaRepository<Donacion, Integer> {

    // Para el panel del refugio: "Ver donaciones recibidas"
    List<Donacion> findByRefugio(Refugio refugio);

    // --- ¡NUEVO MÉTODO AÑADIDO! ---
    // Para el panel del adoptante: "Ver mis donaciones"
    List<Donacion> findByDonante(Donante donante);
}
package com.matchpet.backend_user.repository;

import com.matchpet.backend_user.model.Animal;
import com.matchpet.backend_user.model.SolicitudAdopcion;
import com.matchpet.backend_user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {

    /**
     * Busca todas las solicitudes hechas por un adoptante específico.
     */
    List<SolicitudAdopcion> findByAdoptante(UserModel adoptante);

    /**
     * Busca todas las solicitudes recibidas para un animal específico.
     */
    List<SolicitudAdopcion> findByAnimal(Animal animal);

    /**
     * Busca todas las solicitudes de todos los animales que pertenecen a un refugio.
     * (Esta es más avanzada, la usaremos después)
     * * Spring Data JPA entiende esto como:
     * "Busca Solicitudes donde el 'animal' de la solicitud tiene un 'refugio'
     * cuyo 'id' es el que te estoy pasando"
     */
    List<SolicitudAdopcion> findByAnimal_Refugio_Id(Integer refugioId);
}
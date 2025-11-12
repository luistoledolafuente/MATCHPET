package com.matchpet.backend_user.service;

import com.matchpet.backend_user.dto.solicitud.CreateSolicitudRequest;
import com.matchpet.backend_user.dto.solicitud.SolicitudResponseDTO;
import com.matchpet.backend_user.dto.solicitud.UpdateSolicitudRequest;

import java.util.List;

public interface SolicitudAdopcionService {

    // --- ACCIONES DEL ADOPTANTE ---

    /**
     * Un adoptante crea una nueva solicitud de adopción para un animal.
     * @param request El DTO que contiene el animalId.
     * @param adoptanteEmail El email del adoptante (obtenido del token).
     * @return La solicitud recién creada.
     */
    SolicitudResponseDTO createSolicitud(CreateSolicitudRequest request, String adoptanteEmail);

    /**
     * Un adoptante ve todas las solicitudes que ha enviado.
     * @param adoptanteEmail El email del adoptante (obtenido del token).
     * @return Una lista de sus solicitudes.
     */
    List<SolicitudResponseDTO> getMisSolicitudes(String adoptanteEmail);


    // --- ACCIONES DEL REFUGIO ---

    /**
     * Un refugio ve todas las solicitudes que ha recibido para sus animales.
     * @param refugioEmail El email del usuario del refugio (obtenido del token).
     * @return Una lista de solicitudes recibidas.
     */
    List<SolicitudResponseDTO> getSolicitudesRecibidas(String refugioEmail);

    /**
     * Un refugio actualiza una solicitud (la aprueba, rechaza, o añade notas).
     * @param solicitudId El ID de la solicitud a modificar.
     * @param request El DTO con los nuevos datos (estadoId, mensajes).
     * @param refugioEmail El email del usuario del refugio (para seguridad).
     * @return La solicitud actualizada.
     */
    SolicitudResponseDTO updateSolicitud(Integer solicitudId, UpdateSolicitudRequest request, String refugioEmail);
}
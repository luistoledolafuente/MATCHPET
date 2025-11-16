-- ===============================================
-- MATCHPET DATABASE - MYSQL DATA INSERTION SCRIPT
-- ===============================================
-- Author: (Tu nombre/equipo aquí)
-- Description: Sample data for MatchPet Adoption System
-- Version: 1.0
-- ===============================================

USE db_matchpet;

-- ===============================================
-- 1. INSERT TABLAS DE CONSULTA (Generos, Roles, Estados, etc.)
-- ===============================================
INSERT INTO Generos (genero_id, nombre) VALUES
    (1, 'Macho'),
    (2, 'Hembra');

INSERT INTO Tamanos (tamano_id, nombre) VALUES
    (1, 'Pequeño'),
    (2, 'Mediano'),
    (3, 'Grande');

INSERT INTO Niveles_Energia (nivel_energia_id, nombre) VALUES
    (1, 'Bajo'),
    (2, 'Medio'),
    (3, 'Alto');

INSERT INTO Estados_Adopcion (estado_adopcion_id, nombre) VALUES
    (1, 'Disponible'),
    (2, 'En proceso'),
    (3, 'Adoptado');

INSERT INTO Estados_Solicitud (estado_solicitud_id, nombre) VALUES
    (1, 'Enviada'),
    (2, 'En revisión'),
    (3, 'Aprobada'),
    (4, 'Rechazada');

INSERT INTO Estados_Pago (estado_pago_id, nombre) VALUES
    (1, 'Pendiente'),
    (2, 'Completado'),
    (3, 'Fallido'),
    (4, 'Reembolsado');

INSERT INTO Roles (rol_id, nombre_rol) VALUES
    (1, 'Adoptante'),
    (2, 'Refugio'),
    (3, 'SuperAdmin');

-- ===============================================
-- 2. INSERT Especies, Razas y Temperamentos
-- ===============================================
INSERT INTO Especies (especie_id, nombre_especie) VALUES
    (1, 'Perro'),
    (2, 'Gato'),
    (3, 'Conejo');

INSERT INTO Razas (raza_id, nombre_raza, especie_id) VALUES
    (1, 'Labrador', 1),
    (2, 'Mestizo (Perro)', 1),
    (3, 'Bulldog', 1),
    (4, 'Siamés', 2),
    (5, 'Persa', 2),
    (6, 'Mestizo (Gato)', 2),
    (7, 'Holandés Enano', 3);

INSERT INTO Temperamentos (temperamento_id, nombre_temperamento) VALUES
    (1, 'Juguetón'),
    (2, 'Tranquilo'),
    (3, 'Tímido'),
    (4, 'Energético'),
    (5, 'Cariñoso'),
    (6, 'Independiente'),
    (7, 'Curioso');

-- ===============================================
-- 3. INSERT Usuarios (Adoptantes, Refugios, Admins)
-- ===============================================
INSERT INTO Usuarios (usuario_id, email, hash_contraseña, nombre_completo, telefono, esta_activo) VALUES
    (1, 'maria.lopez@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Maria Lopez', '987654321', true),
    (2, 'carlos.gomez@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Carlos Gomez', '987123456', true),
    (3, 'refugio.patitas@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Usuario Refugio Patitas', '999888777', true),
    (4, 'admin@matchpet.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Admin General', '911222333', true),
    (5, 'refugio.canes@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Usuario Refugio Canes', '955444333', true);

-- ===============================================
-- 4. INSERT Roles de Usuario y Perfiles
-- ===============================================
INSERT INTO Usuario_Roles (usuario_id, rol_id) VALUES
    (1, 1), -- Maria es Adoptante
    (2, 1), -- Carlos es Adoptante
    (3, 2), -- 'refugio.patitas' es Refugio
    (4, 3), -- 'admin' es SuperAdmin
    (5, 2); -- 'refugio.canes' es Refugio

INSERT INTO Perfil_Adoptante (usuario_id, fecha_nacimiento, direccion, ciudad) VALUES
    (1, '1990-05-15', 'Av. Siempre Viva 123', 'Lima'),
    (2, '1985-11-20', 'Calle Falsa 456', 'Arequipa');

-- ===============================================
-- 5. INSERT Refugios
-- ===============================================
INSERT INTO Refugios (refugio_id, nombre, direccion, ciudad, telefono, email, persona_contacto) VALUES
    (1, 'Refugio Patitas Felices', 'Jr. Los Olivos 789', 'Lima', '014567890', 'contacto@patitasfelices.com', 'Ana Torres'),
    (2, 'Canes y Amigos', 'Av. Arequipa 1020', 'Lima', '012345678', 'info@canesy_amigos.com', 'Luis Vera');

-- ===============================================
-- 6. INSERT Perfil_Refugio (Vincular Usuario-Refugio)
-- ===============================================
INSERT INTO Perfil_Refugio (usuario_id, refugio_id) VALUES
    (3, 1), -- El usuario 3 maneja el Refugio 1
    (5, 2); -- El usuario 5 maneja el Refugio 2

-- ===============================================
-- 7. INSERT Animales
-- ===============================================
INSERT INTO Animales (animal_id, nombre, raza_id, refugio_id, fecha_nacimiento_aprox, genero_id, tamano_id, descripcion_personalidad, nivel_energia_id, compatible_niños, compatible_otras_mascotas, esta_vacunado, esta_esterilizado, estado_adopcion_id, fecha_ingreso_refugio) VALUES
    (1, 'Fido', 2, 1, '2023-01-10', 1, 2, 'Muy amigable y le encanta jugar a la pelota. Es un perro guardián.', 3, true, true, true, true, 1, '2023-05-20'),
    (2, 'Misha', 4, 1, '2022-03-15', 2, 1, 'Una gata tranquila y algo tímida al principio, pero muy cariñosa cuando entra en confianza.', 1, true, false, true, true, 2, '2023-10-01'),
    (3, 'Rocky', 1, 2, '2020-07-01', 1, 3, 'Un labrador lleno de energía. Necesita espacio para correr. Adora el agua.', 3, true, true, true, false, 1, '2023-11-01'),
    (4, 'Pelusa', 6, 1, '2023-08-05', 2, 1, 'Gatita encontrada en la calle, muy curiosa y juguetona.', 2, true, true, true, false, 1, '2023-11-05'),
    (5, 'Tambor', 7, 2, '2023-04-01', 1, 1, 'Conejo enano muy dócil. Acostumbrado a estar con personas.', 2, true, true, true, true, 3, '2023-06-10');

-- ===============================================
-- 8. INSERT Fotos y Temperamentos de Animales
-- ===============================================
INSERT INTO Animal_Fotos (foto_id, animal_id, url_foto, es_principal) VALUES
    (1, 1, 'https://example.com/fotos/fido1.jpg', true),
    (2, 1, 'https://example.com/fotos/fido2.jpg', false),
    (3, 2, 'https://example.com/fotos/misha1.jpg', true),
    (4, 3, 'https://example.com/fotos/rocky1.jpg', true),
    (5, 3, 'https://example.com/fotos/rocky2.jpg', false),
    (6, 4, 'https://example.com/fotos/pelusa1.jpg', true),
    (7, 5, 'https://example.com/fotos/tambor1.jpg', true);

INSERT INTO Animal_Temperamentos (animal_id, temperamento_id) VALUES
    (1, 1), -- Fido: Juguetón
    (1, 4), -- Fido: Energético
    (1, 5), -- Fido: Cariñoso
    (2, 2), -- Misha: Tranquilo
    (2, 3), -- Misha: Tímido
    (2, 6), -- Misha: Independiente
    (3, 1), -- Rocky: Juguetón
    (3, 4), -- Rocky: Energético
    (4, 1), -- Pelusa: Juguetón
    (4, 7), -- Pelusa: Curioso
    (5, 2); -- Tambor: Tranquilo

-- ===============================================
-- 9. INSERT Solicitudes de Adopción
-- ===============================================
INSERT INTO Solicitudes_Adopcion (solicitud_id, usuario_id, animal_id, estado_solicitud_id, notas_internas, mensaje_al_adoptante) VALUES
    (1, 1, 2, 2, 'La adoptante Maria Lopez (ID 1) parece una buena candidata. Vive en depa, pero el gato es tranquilo.', 'Gracias por tu solicitud para Misha. Estamos revisando tu perfil.'),
    (2, 2, 5, 3, 'Carlos Gomez (ID 2) ya tiene experiencia con conejos. Aprobado.', '¡Felicidades! Tu solicitud para Tambor ha sido aprobada. Nos pondremos en contacto para coordinar la entrega.'),
    (3, 1, 3, 4, 'La adoptante vive en un depa muy pequeño, no es ideal para un Labrador energético.', 'Lamentamos informarte que tu solicitud para Rocky ha sido rechazada. Creemos que no se adaptaría bien a tu espacio.');

-- ===============================================
-- 10. INSERT Donantes y Donaciones
-- ===============================================
INSERT INTO Donantes (donante_id, nombre_completo, email, usuario_id) VALUES
    (1, 'Maria Lopez', 'maria.lopez@email.com', 1), -- Donante vinculado a usuario
    (2, 'Carlos Gomez', 'carlos.gomez@email.com', 2), -- Donante vinculado a usuario
    (3, 'Lucia Fernandez', 'lucia.f@email-anonimo.com', NULL); -- Donante anónimo

INSERT INTO Donaciones (donacion_id, donante_id, refugio_id, animal_id, monto, moneda, estado_pago_id, gateway_transaccion_id) VALUES
    (1, 1, 1, NULL, 100.00, 'PEN', 2, 'PAYPAL_TXN_111222333'), -- Maria donó S/100 al Refugio 1
    (2, 3, NULL, 3, 50.00, 'PEN', 2, 'STRIPE_TXN_444555666'), -- Lucia donó S/50 para Rocky (Animal 3)
    (3, 2, 2, NULL, 75.50, 'PEN', 1, NULL); -- Carlos intentó donar S/75.50 al Refugio 2 (Pendiente)

-- ===============================================
-- END OF DATA INSERTION
-- ===============================================
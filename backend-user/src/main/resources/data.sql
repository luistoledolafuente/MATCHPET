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
-- Inserción de Géneros
INSERT INTO Generos (nombre) VALUES
                                 ('Macho'),
                                 ('Hembra');

-- Inserción de Tamaños
INSERT INTO Tamanos (nombre) VALUES
                                 ('Pequeño'),
                                 ('Mediano'),
                                 ('Grande');

-- Inserción de Niveles de Energía
INSERT INTO Niveles_Energia (nombre) VALUES
                                         ('Bajo'),
                                         ('Medio'),
                                         ('Alto');

-- Inserción de Estados de Adopción
INSERT INTO Estados_Adopcion (nombre) VALUES
                                          ('Disponible'),
                                          ('En proceso'),
                                          ('Adoptado');

-- Inserción de Roles
INSERT INTO Roles (nombre_rol) VALUES
                                   ('Adoptante'),
                                   ('Refugio'),
                                   ('SuperAdmin');

-- ===============================================
-- 2. INSERT Especies, Razas y Temperamentos
-- ===============================================
-- Inserción de Especies
INSERT INTO Especies (nombre_especie) VALUES
                                          ('Perro'),
                                          ('Gato'),
                                          ('Conejo');

-- Inserción de Razas
INSERT INTO Razas (nombre_raza, especie_id) VALUES
                                                ('Labrador', 1),  -- Perro
                                                ('Mestizo (Perro)', 1),  -- Perro
                                                ('Bulldog', 1),   -- Perro
                                                ('Siamés', 2),    -- Gato
                                                ('Persa', 2),     -- Gato
                                                ('Mestizo (Gato)', 2),   -- Gato
                                                ('Holandés Enano', 3);  -- Conejo

-- Inserción de Temperamentos
INSERT INTO Temperamentos (nombre_temperamento) VALUES
                                                    ('Juguetón'),
                                                    ('Tranquilo'),
                                                    ('Tímido'),
                                                    ('Energético'),
                                                    ('Cariñoso'),
                                                    ('Independiente'),
                                                    ('Curioso');

-- ===============================================
-- 3. INSERT Usuarios (Adoptantes, Refugios, Admins)
-- ===============================================
-- Inserción de Usuarios
INSERT INTO Usuarios (email, hash_contraseña, nombre, apellido_paterno, apellido_materno, telefono, esta_activo) VALUES
                                                                                                                     ('juan.perez@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Juan Pérez', 'Pérez', 'González', '987654321', true),
                                                                                                                     ('marta.fernandez@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Marta Fernández', 'Fernández', 'Gómez', '987123456', true),
                                                                                                                     ('refugio.manosamiga@email.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Refugio Manos Amigas', 'Manos', 'Amigas', '999888777', true),
                                                                                                                     ('admin@matchpet.com', '$2a$10$f.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5x.xP5', 'Admin General', 'General', 'Admin', '911222333', true);

-- ===============================================
-- 4. INSERT Roles de Usuario y Perfiles
-- ===============================================
-- Roles de Usuario
INSERT INTO Usuario_Roles (usuario_id, rol_id) VALUES
                                                   (1, 1), -- Juan es Adoptante
                                                   (2, 1), -- Marta es Adoptante
                                                   (3, 2), -- Refugio Manos Amigas es Refugio
                                                   (4, 3); -- Admin es SuperAdmin

-- Perfil Adoptante
INSERT INTO Perfil_Adoptante (usuario_id, fecha_nacimiento, direccion, ciudad, pais) VALUES
                                                                                         (1, '1985-07-25', 'Av. Los Olivos 345', 'Lima', 'Perú'),
                                                                                         (2, '1990-11-30', 'Calle Falsa 567', 'Arequipa', 'Perú');

-- ===============================================
-- 5. INSERT Refugios
-- ===============================================
-- Inserción de Refugios
INSERT INTO Refugios (nombre, direccion, ciudad, telefono, email, persona_contacto) VALUES
                                                                                        ('Manos Amigas', 'Jr. San Pedro 123', 'Lima', '014567890', 'contacto@manosamigas.com', 'Ana López'),
                                                                                        ('Corazón de Perro', 'Av. Pardo 456', 'Arequipa', '012345678', 'contacto@corazondeperro.com', 'Carlos Herrera');

-- ===============================================
-- 6. INSERT Perfil_Refugio (Vincular Usuario-Refugio)
-- ===============================================
-- Perfil de Refugio
INSERT INTO Perfil_Refugio (usuario_id, refugio_id) VALUES
                                                        (3, 1), -- Usuario Refugio Manos Amigas
                                                        (4, 2); -- Admin maneja el Refugio 2

-- ===============================================
-- 7. INSERT Animales
-- ===============================================
-- Inserción de Animales
INSERT INTO Animales (nombre, raza_id, refugio_id, fecha_nacimiento_aprox, genero_id, tamano_id, descripcion_personalidad, nivel_energia_id, compatible_niños, compatible_otras_mascotas, esta_vacunado, esta_esterilizado, estado_adopcion_id, fecha_ingreso_refugio) VALUES
                                                                                                                                                                                                                                                                           ('Max', 2, 1, '2022-06-10', 1, 2, 'Perro activo, le encanta jugar con niños y otros perros.', 3, true, true, true, true, 1, '2023-04-20'),
                                                                                                                                                                                                                                                                           ('Luna', 4, 1, '2021-12-05', 2, 1, 'Gata tranquila, se adapta bien a la vida en departamento.', 1, true, true, true, false, 1, '2023-05-10'),
                                                                                                                                                                                                                                                                           ('Rocky', 1, 2, '2021-07-01', 1, 3, 'Perro juguetón, siempre está lleno de energía. Ideal para correr.', 3, true, false, true, true, 1, '2023-06-15'),
                                                                                                                                                                                                                                                                           ('Bella', 5, 2, '2022-04-17', 2, 2, 'Gata muy tranquila, le gusta la compañía humana.', 2, true, true, true, true, 1, '2023-07-05');

-- ===============================================
-- 8. INSERT Fotos y Temperamentos de Animales
-- ===============================================
-- Fotos de Animales
INSERT INTO Animal_Fotos (animal_id, url_foto, es_principal) VALUES
                                                                 (1, 'https://example.com/fotos/max1.jpg', true),
                                                                 (2, 'https://example.com/fotos/luna1.jpg', true),
                                                                 (3, 'https://example.com/fotos/rocky1.jpg', true),
                                                                 (4, 'https://example.com/fotos/bella1.jpg', true);

-- Temperamentos de los Animales
INSERT INTO Animal_Temperamentos (animal_id, temperamento_id) VALUES
                                                                  (1, 1), -- Max: Juguetón
                                                                  (1, 4), -- Max: Energético
                                                                  (2, 2), -- Luna: Tranquila
                                                                  (3, 1), -- Rocky: Juguetón
                                                                  (3, 4), -- Rocky: Energético
                                                                  (4, 2); -- Bella: Tranquila

-- ===============================================
-- 9. INSERT Solicitudes de Adopción
-- ===============================================
-- Solicitudes de Adopción
INSERT INTO Solicitudes_Adopcion (usuario_id, animal_id, estado_solicitud_id, notas_internas, mensaje_al_adoptante) VALUES
                                                                                                                        (1, 2, 2, 'Juan tiene un departamento pequeño, no es ideal para Luna.', 'Gracias por tu solicitud para Luna, estamos revisando tu perfil.'),
                                                                                                                        (2, 1, 3, 'Marta tiene un jardín grande y experiencia con perros. Aprobado.', '¡Felicidades! Tu solicitud para Max ha sido aprobada. Nos pondremos en contacto para coordinar la entrega.');
(2, 2, 1, 3, 'Marta tiene un jardín grande y experiencia con perros. Aprobado.', '¡Felicidades! Tu solicitud para Max ha sido aprobada. Nos pondremos en contacto para coordinar la entrega.');

-- ===============================================
-- END OF DATA INSERTION
-- ===============================================

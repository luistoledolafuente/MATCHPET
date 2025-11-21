-- ===============================================
-- MATCHPET DATABASE - MYSQL SCHEMA CREATION SCRIPT
-- ===============================================
-- Author: MatchPet System
-- Description: MySQL Database schema for MatchPet Adoption System
-- Version: 1.2 (Final: Consistencia total con el Backend Java)
-- ===============================================

-- ===============================================
-- 0. CREACIÓN Y SELECCIÓN DE LA BASE DE DATOS
-- ===============================================
DROP DATABASE IF EXISTS db_matchpet;
CREATE DATABASE db_matchpet
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE db_matchpet;

-- ===============================================
-- DROP TABLES (Orden inverso por dependencias)
-- ===============================================
DROP TABLE IF EXISTS PasswordResetTokens;
DROP TABLE IF EXISTS Donaciones;
DROP TABLE IF EXISTS Donantes;
DROP TABLE IF EXISTS Solicitudes_Adopcion;
DROP TABLE IF EXISTS Animal_Temperamentos;
DROP TABLE IF EXISTS Animal_Fotos;
DROP TABLE IF EXISTS Animales;
DROP TABLE IF EXISTS Razas;
DROP TABLE IF EXISTS Temperamentos;
DROP TABLE IF EXISTS Especies;
DROP TABLE IF EXISTS Perfil_Refugio;
DROP TABLE IF EXISTS Refugios;
DROP TABLE IF EXISTS Perfil_Adoptante;
DROP TABLE IF EXISTS Usuario_Roles;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Estados_Pago;
DROP TABLE IF EXISTS Estados_Solicitud;
DROP TABLE IF EXISTS Estados_Adopcion;
DROP TABLE IF EXISTS Niveles_Energia;
DROP TABLE IF EXISTS Tamanos;
DROP TABLE IF EXISTS Generos;


-- ===============================================
-- 1. TABLAS DE CONSULTA (Reemplazo de ENUMs)
-- ===============================================

CREATE TABLE IF NOT EXISTS Generos (
                                       genero_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Tamanos (
                                       tamano_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Niveles_Energia (
                                               nivel_energia_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                               nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Estados_Adopcion (
                                                estado_adopcion_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Estados_Solicitud (
                                                 estado_solicitud_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                                 nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Estados_Pago (
                                            estado_pago_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                            nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Roles (
                                     rol_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     nombre_rol VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ===============================================
-- 2. TABLAS PRINCIPALES DEL SISTEMA
-- ===============================================

-- ===============================================
-- TABLE: Usuarios (CORREGIDO: Nombres y Teléfono)
-- ===============================================
CREATE TABLE IF NOT EXISTS Usuarios (
                                        usuario_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                        email VARCHAR(255) NOT NULL UNIQUE,
    hash_contraseña TEXT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    fecha_creacion_perfil TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    esta_activo BOOLEAN NOT NULL DEFAULT true
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Usuario_Roles
-- ===============================================
CREATE TABLE IF NOT EXISTS Usuario_Roles (
                                             usuario_id INT NOT NULL,
                                             rol_id INT NOT NULL,
                                             PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES Usuarios (usuario_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES Roles (rol_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_usuarioroles_rol_id ON Usuario_Roles (rol_id);

-- ===============================================
-- TABLE: Especies, Temperamentos, Razas
-- ===============================================
CREATE TABLE IF NOT EXISTS Especies (
                                        especie_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                        nombre_especie VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Temperamentos (
                                             temperamento_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                             nombre_temperamento VARCHAR(100) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Razas (
                                     raza_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     nombre_raza VARCHAR(100) NOT NULL,
    especie_id INT NOT NULL,
    FOREIGN KEY (especie_id) REFERENCES Especies (especie_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT unique_raza_especie UNIQUE (nombre_raza, especie_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_razas_especie_id ON Razas (especie_id);

-- ===============================================
-- TABLE: Animales
-- ===============================================
CREATE TABLE IF NOT EXISTS Animales (
                                        animal_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                        nombre VARCHAR(100) NOT NULL,
    raza_id INT NOT NULL,
    refugio_id INT NOT NULL,
    fecha_nacimiento_aprox DATE NOT NULL,
    genero_id INT NOT NULL,
    tamano_id INT NOT NULL,
    descripcion_personalidad TEXT NOT NULL,
    nivel_energia_id INT NOT NULL,
    compatible_niños BOOLEAN NOT NULL DEFAULT false,
    compatible_otras_mascotas BOOLEAN NOT NULL DEFAULT false,
    esta_vacunado BOOLEAN NOT NULL DEFAULT false,
    esta_esterilizado BOOLEAN NOT NULL DEFAULT false,
    historial_medico TEXT NOT NULL,
    estado_adopcion_id INT NOT NULL,
    fecha_ingreso_refugio DATE NOT NULL DEFAULT (CURRENT_DATE),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (raza_id) REFERENCES Razas (raza_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (refugio_id) REFERENCES Refugios (refugio_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (genero_id) REFERENCES Generos (genero_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (tamano_id) REFERENCES Tamanos (tamano_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (nivel_energia_id) REFERENCES Niveles_Energia (nivel_energia_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (estado_adopcion_id) REFERENCES Estados_Adopcion (estado_adopcion_id)
                                                                     ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_animales_raza_id ON Animales (raza_id);
CREATE INDEX idx_animales_refugio_id ON Animales (refugio_id);
CREATE INDEX idx_animales_genero_id ON Animales (genero_id);
CREATE INDEX idx_animales_tamano_id ON Animales (tamano_id);
CREATE INDEX idx_animales_nivel_energia_id ON Animales (nivel_energia_id);
CREATE INDEX idx_animales_estado_adopcion_id ON Animales (estado_adopcion_id);

-- ===============================================
-- 3. CARGA DE DATOS INICIALES (Tablas de Consulta)
-- ===============================================
INSERT INTO Generos (nombre) VALUES ('Macho'), ('Hembra');
INSERT INTO Tamanos (nombre) VALUES ('Pequeño'), ('Mediano'), ('Grande');
INSERT INTO Niveles_Energia (nombre) VALUES ('Bajo'), ('Medio'), ('Alto');
INSERT INTO Estados_Adopcion (nombre) VALUES ('Disponible'), ('En proceso'), ('Adoptado');
INSERT INTO Estados_Solicitud (nombre) VALUES ('Enviada'), ('En revisión'), ('Aprobada'), ('Rechazada');
INSERT INTO Estados_Pago (nombre) VALUES ('Pendiente'), ('Completado'), ('Fallido'), ('Reembolsado');
INSERT INTO Roles (nombre_rol) VALUES ('Adoptante'), ('Refugio'), ('SuperAdmin');

-- ===============================================
-- END OF SCHEMA CREATION
-- ===============================================

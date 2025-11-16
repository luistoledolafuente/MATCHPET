-- ===============================================
-- MATCHPET DATABASE - MYSQL SCHEMA CREATION SCRIPT
-- ===============================================
-- Author: (Tu nombre/equipo aquí)
-- Description: MySQL Database schema for MatchPet Adoption System
-- Version: 1.1 (Campos NOT NULL aplicados)
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

-- ===============================================
-- TABLE: Generos
-- Description: Almacena los géneros (Ej: Macho, Hembra)
-- ===============================================
CREATE TABLE IF NOT EXISTS Generos (
    genero_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Tamanos
-- Description: Almacena los tamaños (Ej: Pequeño, Mediano)
-- ===============================================
CREATE TABLE IF NOT EXISTS Tamanos (
    tamano_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Niveles_Energia
-- Description: Almacena los niveles de energía (Ej: Bajo, Medio)
-- ===============================================
CREATE TABLE IF NOT EXISTS Niveles_Energia (
    nivel_energia_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Estados_Adopcion
-- Description: Estados de una mascota (Ej: Disponible, Adoptado)
-- ===============================================
CREATE TABLE IF NOT EXISTS Estados_Adopcion (
    estado_adopcion_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Estados_Solicitud
-- Description: Estados de una solicitud (Ej: Enviada, Aprobada)
-- ===============================================
CREATE TABLE IF NOT EXISTS Estados_Solicitud (
    estado_solicitud_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Estados_Pago
-- Description: Estados de una donación (Ej: Pendiente, Completado)
-- ===============================================
CREATE TABLE IF NOT EXISTS Estados_Pago (
    estado_pago_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Roles
-- Description: Roles del sistema (Ej: Adoptante, Refugio)
-- ===============================================
CREATE TABLE IF NOT EXISTS Roles (
    rol_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ===============================================
-- 2. TABLAS PRINCIPALES DEL SISTEMA
-- ===============================================

-- ===============================================
-- TABLE: Usuarios
-- Description: Almacena info de login (email/pass) de todos los usuarios
-- ===============================================
CREATE TABLE IF NOT EXISTS Usuarios (
    usuario_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    hash_contraseña TEXT NOT NULL,
    nombre_completo VARCHAR(255) NOT NULL,
    telefono VARCHAR(20), -- Opcional, puede ser NULL
    fecha_creacion_perfil TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    esta_activo BOOLEAN NOT NULL DEFAULT true
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Usuario_Roles (Relación Usuario-Rol)
-- Description: Tabla pivote M2M para asignar roles a usuarios
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
-- TABLE: Perfil_Adoptante
-- Description: Info adicional específica para usuarios 'Adoptante'
-- ===============================================
CREATE TABLE IF NOT EXISTS Perfil_Adoptante (
    usuario_id INT NOT NULL PRIMARY KEY,
    fecha_nacimiento DATE, -- Opcional, puede ser NULL
    direccion TEXT, -- Opcional, puede ser NULL
    ciudad VARCHAR(100), -- Opcional, puede ser NULL

    FOREIGN KEY (usuario_id) REFERENCES Usuarios (usuario_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Refugios
-- Description: Almacena información de las organizaciones (refugios)
-- ===============================================
CREATE TABLE IF NOT EXISTS Refugios (
    refugio_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    direccion TEXT NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    telefono VARCHAR(20), -- Opcional, puede ser NULL
    email VARCHAR(255) NOT NULL UNIQUE,
    persona_contacto VARCHAR(255), -- Opcional, puede ser NULL
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Perfil_Refugio
-- Description: Vincula una cuenta de Usuario con un registro de Refugio
-- ===============================================
CREATE TABLE IF NOT EXISTS Perfil_Refugio (
    usuario_id INT NOT NULL PRIMARY KEY,
    refugio_id INT NOT NULL UNIQUE,

    FOREIGN KEY (usuario_id) REFERENCES Usuarios (usuario_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (refugio_id) REFERENCES Refugios (refugio_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_perfilrefugio_refugio_id ON Perfil_Refugio (refugio_id);

-- ===============================================
-- TABLE: Especies
-- Description: Almacena tipos de especies (Ej: Perro, Gato)
-- ===============================================
CREATE TABLE IF NOT EXISTS Especies (
    especie_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_especie VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Temperamentos
-- Description: Almacena temperamentos de mascotas (Ej: Juguetón)
-- ===============================================
CREATE TABLE IF NOT EXISTS Temperamentos (
    temperamento_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_temperamento VARCHAR(100) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================================
-- TABLE: Razas
-- Description: Almacena razas de mascotas, vinculadas a una Especie
-- ===============================================
CREATE TABLE IF NOT EXISTS Razas (
    raza_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_raza VARCHAR(100) NOT NULL UNIQUE,
    especie_id INT NOT NULL,

    FOREIGN KEY (especie_id) REFERENCES Especies (especie_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_razas_especie_id ON Razas (especie_id);

-- ===============================================
-- TABLE: Animales
-- Description: Almacena la información de las mascotas en adopción
-- ===============================================
CREATE TABLE IF NOT EXISTS Animales (
    animal_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    raza_id INT NOT NULL,
    refugio_id INT NOT NULL,
    fecha_nacimiento_aprox DATE, -- Opcional, puede ser NULL
    genero_id INT NOT NULL,
    tamano_id INT NOT NULL, -- CAMBIO: Hecho NOT NULL
    descripcion_personalidad TEXT, -- Opcional, puede ser NULL
    nivel_energia_id INT NOT NULL, -- CAMBIO: Hecho NOT NULL
    compatible_niños BOOLEAN NOT NULL DEFAULT false, -- CAMBIO: Hecho NOT NULL
    compatible_otras_mascotas BOOLEAN NOT NULL DEFAULT false, -- CAMBIO: Hecho NOT NULL
    esta_vacunado BOOLEAN NOT NULL DEFAULT false, -- CAMBIO: Hecho NOT NULL
    esta_esterilizado BOOLEAN NOT NULL DEFAULT false, -- CAMBIO: Hecho NOT NULL
    historial_medico TEXT, -- Opcional, puede ser NULL
    estado_adopcion_id INT NOT NULL,
    fecha_ingreso_refugio DATE NOT NULL DEFAULT (CURRENT_DATE), -- CAMBIO: Hecho NOT NULL
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
-- TABLE: Animal_Fotos
-- Description: Almacena URLs de fotos para cada animal
-- ===============================================
CREATE TABLE IF NOT EXISTS Animal_Fotos (
    foto_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    animal_id INT NOT NULL,
    url_foto TEXT NOT NULL,
    es_principal BOOLEAN NOT NULL DEFAULT false,

    FOREIGN KEY (animal_id) REFERENCES Animales (animal_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_animalfotos_animal_id ON Animal_Fotos (animal_id);

-- ===============================================
-- TABLE: Animal_Temperamentos (Relación Animal-Temperamento)
-- Description: Tabla pivote M2M para asignar temperamentos a animales
-- ===============================================
CREATE TABLE IF NOT EXISTS Animal_Temperamentos (
    animal_id INT NOT NULL,
    temperamento_id INT NOT NULL,
    PRIMARY KEY (animal_id, temperamento_id),

    FOREIGN KEY (animal_id) REFERENCES Animales (animal_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (temperamento_id) REFERENCES Temperamentos (temperamento_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_animaltemperamentos_temperamento_id ON Animal_Temperamentos (temperamento_id);

-- ===============================================
-- TABLE: Solicitudes_Adopcion
-- Description: Almacena las solicitudes enviadas por adoptantes
-- ===============================================
CREATE TABLE IF NOT EXISTS Solicitudes_Adopcion (
    solicitud_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    animal_id INT NOT NULL,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_solicitud_id INT NOT NULL,
    notas_internas TEXT, -- Opcional (para el refugio), puede ser NULL
    mensaje_al_adoptante TEXT, -- Opcional, puede ser NULL
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (usuario_id) REFERENCES Usuarios (usuario_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (animal_id) REFERENCES Animales (animal_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (estado_solicitud_id) REFERENCES Estados_Solicitud (estado_solicitud_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_solicitudes_usuario_id ON Solicitudes_Adopcion (usuario_id);
CREATE INDEX idx_solicitudes_animal_id ON Solicitudes_Adopcion (animal_id);
CREATE INDEX idx_solicitudes_estado_id ON Solicitudes_Adopcion (estado_solicitud_id);

-- ===============================================
-- TABLE: Donantes
-- Description: Almacena info de donantes (pueden ser usuarios o anónimos)
-- ===============================================
CREATE TABLE IF NOT EXISTS Donantes (
    donante_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    usuario_id INT UNIQUE, -- Opcional, puede ser NULL si no es usuario registrado
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_donantes_usuario_id ON Donantes (usuario_id);

-- ===============================================
-- TABLE: Donaciones
-- Description: Registra todas las transacciones de donación
-- ===============================================
CREATE TABLE IF NOT EXISTS Donaciones (
    donacion_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    donante_id INT NOT NULL,
    refugio_id INT,  -- Opcional, puede ser NULL (donación general)
    animal_id INT,   -- Opcional, puede ser NULL (donación general)
    monto DECIMAL(10, 2) NOT NULL,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    fecha_donacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_pago_id INT NOT NULL,
    gateway_transaccion_id VARCHAR(255) UNIQUE, -- Opcional, puede ser NULL
    mensaje_donante TEXT -- Opcional, puede ser NULL
    ,

    FOREIGN KEY (donante_id) REFERENCES Donantes (donante_id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (refugio_id) REFERENCES Refugios (refugio_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (animal_id) REFERENCES Animales (animal_id)
    ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (estado_pago_id) REFERENCES Estados_Pago (estado_pago_id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_donaciones_donante_id ON Donaciones (donante_id);
CREATE INDEX idx_donaciones_refugio_id ON Donaciones (refugio_id);
CREATE INDEX idx_donaciones_animal_id ON Donaciones (animal_id);
CREATE INDEX idx_donaciones_estado_pago_id ON Donaciones (estado_pago_id);

-- ===============================================
-- TABLE: PasswordResetTokens (HU-05)
-- Description: Almacena tokens temporales para reseteo de contraseña
-- ===============================================
CREATE TABLE IF NOT EXISTS PasswordResetTokens (
    token_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    usuario_id INT NOT NULL UNIQUE,

    FOREIGN KEY (usuario_id) REFERENCES Usuarios (usuario_id)
    ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_passwordreset_token ON PasswordResetTokens (token);


-- ===============================================
-- 5. CARGA DE DATOS INICIALES (Tablas de Consulta)
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
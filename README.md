# MatchPet - Documentación de Despliegue

Este documento explica cómo desplegar la plataforma MatchPet en un servidor, cubriendo backend y frontend para los distintos módulos.

---

## Requisitos Previos

- **Java 17+** (para Spring Boot)
- **Maven** (opcional si usas `./mvnw`)
- **Node.js** y **npm**
- **Python 3** y **pip** (para Django)
- **MySQL** configurado y activo
- **Android Studio** (para app móvil, solo si se quiere compilar o desplegar)

---

## Configuración de Base de Datos

1. Crear una base de datos MySQL para la aplicación.
2. Actualizar los datos de conexión (host, usuario, contraseña, nombre BD) en:

   - `backend-admin/settings.py` (Django)
   - `backend-user/src/main/resources/application.properties` (Spring Boot)

---

## Despliegue Backend Administrador (Django)

```bash
cd backend-admin
python -m venv env
source env/bin/activate   # Windows: env\Scripts\activate
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver 0.0.0.0:8000
````
---

## Despliegue Backend Usuario (Spring Boot)

```bash
cd backend-user
./mvnw spring-boot:run  # o "mvn spring-boot:run" si Maven está instalado globalmente
```

---

## Despliegue Frontend Web Administrador

```bash
cd frontend-web-admin
npm install
npm run dev
```

---

## Despliegue Frontend Web Usuario

```bash
cd frontend-web-user
npm install
npm run dev
```

---

## Despliegue App Móvil (Kotlin)

1. Abrir la carpeta `frontend-movil` en Android Studio.
2. Configurar la URL base del backend en el código si es necesario.
3. Compilar y generar APK para distribución o instalar en dispositivo/emulador.


---

## Contacto

Para soporte o dudas sobre despliegue, contactar al equipo de desarrollo:
- ailyn.medina@tecsup.edu.pe
- luis.toledo.l@tecsup.edu.pe
- maria.moya@tecsup.edu.pe

---


# Sistema de Citas Médicas

Sistema de Citas Médicas es una aplicación web diseñada para gestionar citas médicas de manera eficiente, permitiendo a pacientes y médicos organizar, consultar y administrar sus citas en línea.

## 🚀 Funcionalidades

- 📅 Gestión de Citas: Reserva, consulta, edición y cancelación de citas médicas.
- 👨‍⚕️ Gestión de Médicos: Visualización de perfiles de médicos y sus especialidades.
- 🏥 Gestión de Pacientes: Registro y administración de información de pacientes.
- 🔒 Autenticación y Seguridad: Registro, inicio de sesión y control de acceso para usuarios y médicos.
- 📊 Historial Médico: Consulta del historial de citas y notas médicas.
- 📍 Localización: Visualización de clínicas y especialidades disponibles.
- 📱 Interfaz Amigable: Diseño intuitivo y fácil de usar para una mejor experiencia del usuario.

## 🛠️ Tecnologías Utilizadas

- Frontend: React + Vite
- Backend: Spring Boot (Java)
- Base de Datos: MySQL
- Estilos: CSS

## 📲 Instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tuusuario/SISTEMA_CITAS_MEDICAS.git
   ```
2. Instala las dependencias del frontend:
   ```bash
   cd sistema_citas_frontend
   npm install
   ```
3. Inicia el frontend:
   ```bash
   npm run dev
   ```
4. Inicia el backend:
   ```bash
   cd ../Sistema_Citas_backend
   ./mvnw spring-boot:run
   ```
5. Accede a la aplicación desde tu navegador en `http://localhost:5173` (o el puerto configurado).

## Autenticación

La aplicación soporta dos métodos de autenticación:

- JWT local (HS256) para login con credenciales
- Google OAuth 2.0 (ID Token RS256)

Ambos tokens son aceptados en las mismas rutas protegidas mediante
un filtro de autenticación híbrido.


## 📄 Pantallas Principales

- Inicio de Sesión y Registro
- Panel de Usuario (Paciente/Doctor)
- Gestión y Búsqueda de Citas
- Perfil de Médico
- Historial de Citas
- Confirmación y Cancelación de Citas

## 👤 Autores

- [Kendra Artavia Caballero](https://github.com/Kendra-Artavia)
- [Alexia Alvarado Alfaro](https://github.com/LittleHehet)
- [William Rodríguez Campos](https://github.com/WillyRC2001)

## 📌 Contexto del Proyecto

Este sistema fue desarrollado como proyecto académico para la gestión de citas médicas, facilitando la interacción entre pacientes y profesionales de la salud. Su objetivo es optimizar la administración de citas y mejorar la experiencia de los usuarios.

## 📃 Licencia

Este proyecto es para uso educativo y personal.


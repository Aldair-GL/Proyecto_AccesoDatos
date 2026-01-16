# 👺 Goblin Market API

Sistema de gestión de inventario y ventas para un mercado de fantasía RPG, desarrollado con **Spring Boot 3**.

## 🚀 Características
- **Gestión de Aventureros (Clientes):** Registro y consulta de perfiles.
- **Inventario Místico (Productos):** Control de stock y precios de objetos mágicos.
- **Sistema de Transacciones (Ventas):** Registro de ventas con validación de stock y persistencia de precios históricos.
- **Documentación Interactiva:** Integración completa con Swagger/OpenAPI.

## 🛠️ Tecnologías
- **Java 17/21**
- **Spring Boot 3** (Data JPA, Web, Validation)
- **MySQL 8**
- **Maven**

## 📦 Instalación y Configuración
1. Clonar el repositorio.
2. Configurar la base de datos en `src/main/resources/application.properties`.
3. Ejecutar `mvn spring-boot:run`.

## 📖 Documentación de la API
Una vez iniciada la aplicación, accede a la interfaz de Swagger en:
`http://localhost:8080/swagger`
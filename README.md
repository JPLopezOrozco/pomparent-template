# Banking Platform - Monolith App 🏦

Este proyecto es una **plataforma bancaria** desarrollada con **Spring Boot 3, PostgreSQL y Docker**.  
En esta primera versión se implementa un **monolito** con las siguientes funcionalidades:

- Gestión de **cuentas bancarias**.
- Registro de **transacciones (créditos y débitos)**.
- Validaciones de negocio (ej. no permitir débitos sin balance suficiente).
- Documentación automática de API con **Swagger/OpenAPI**.
- Migraciones de base de datos con **Flyway**.
- Pruebas unitarias y de integración con **JUnit, Mockito y Testcontainers**.

---

## 🏗️ Arquitectura

```
               ┌──────────────────────────┐
               │      monolith-app        │
               │  (Spring Boot service)   │
               │  Puerto: 8080            │
               └───────────┬──────────────┘
                           │ JDBC
                           │
                           ▼
               ┌──────────────────────────┐
               │      accounts-db         │
               │   PostgreSQL Database    │
               │  Puerto interno: 5432    │
               │  Puerto local: 5433      │
               └──────────────────────────┘
```

---

## 🚀 Ejecutar con Docker

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/banking-platform.git
cd banking-platform/deploy
```

### 2. Levantar servicios con Docker Compose
```bash
docker compose up --build -d
```

Esto levanta dos contenedores:
- **accounts-db** → Base de datos PostgreSQL (puerto local `5433`).
- **monolith-app** → Aplicación Spring Boot (puerto local `8080`).

### 3. Ver logs de la app
```bash
docker compose logs -f monolith-app
```

### 4. Acceder a la aplicación
- API principal: [http://localhost:8080](http://localhost:8080)  
- Documentación Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  

---

## 📚 Endpoints principales

### Cuentas (`/api/accounts`)
- **POST** `/api/accounts` → Crear nueva cuenta.
- **GET** `/api/accounts/{id}` → Obtener cuenta por ID.
- **GET** `/api/accounts` → Listar todas las cuentas.

### Transacciones (`/api/transactions`)
- **POST** `/api/transactions` → Crear nueva transacción (crédito o débito).
- **GET** `/api/transactions/{id}` → Obtener transacción por ID.
- **GET** `/api/transactions/account/{id}` → Listar transacciones de una cuenta (con paginación).

### Documentación API
- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI JSON**: `/v3/api-docs`

---

## ⚙️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Data JPA + Hibernate**
- **PostgreSQL 16**
- **Flyway** (migraciones)
- **Docker & Docker Compose**
- **JUnit 5 + Mockito + Testcontainers**
- **Springdoc OpenAPI 2.8.12** (Swagger UI)

---

## 👨‍💻 Desarrollo local (sin Docker)

Si querés correr la app localmente sin Docker:

1. Levantar PostgreSQL local:
   ```bash
   docker run --name account-db -e POSTGRES_DB=account_db \
   -e POSTGRES_USER=account_user -e POSTGRES_PASSWORD=123456789 \
   -p 5433:5432 -d postgres:16
   ```

2. Configurar `application.yml` con:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/account_db
       username: account_user
       password: 123456789
   ```

3. Ejecutar con Maven:
   ```bash
   mvn spring-boot:run -pl services/monolith-app
   ```
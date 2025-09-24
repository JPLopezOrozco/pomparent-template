# Banking Platform - Microservices 🏦

Plataforma bancaria basada en **microservicios** con **Spring Boot 3**, **Spring Cloud** (**Config Server, Eureka, Gateway**) y **Docker Compose**.  
Incluye **idempotencia**, **autenticación JWT** y **hardening de dependencias**.

---
# 🏗️ Arquitectura

                        ┌─────────────────────────────┐
                        │        api-gateway          │
                        │  (Spring Cloud Gateway)     │
                        │  Puerto: 8080               │
                        └─────────────┬───────────────┘
                                      │
                                      ▼
         ┌────────────────────┐   ┌────────────────────┐
         │  account-service   │   │ transaction-service│
         │  Puerto: 8081      │   │  Puerto: 8082      │
         └──────────┬─────────┘   └──────────┬─────────┘
                    │                        │
                    ▼                        ▼
         ┌────────────────────┐   ┌────────────────────┐
         │   accounts-db      │   │ transactions-db    │
         │ PostgreSQL:5433    │   │ PostgreSQL:5434    │
         └────────────────────┘   └────────────────────┘


           ┌───────────────────────┐
           │    config-server      │──► Lee configs desde:
           │ Puerto: 8888          │    banking-platform-config (Git)
           └───────────────────────┘

           ┌───────────────────────┐
           │    eureka-server      │
           │ Puerto: 8761          │
           └───────────────────────┘


           ┌───────────────────────┐          (emite JWT HS256)
           │     auth-service      │ ◄────────────────────────────────┐
           │ Puerto: 9090          │                                  │
           └──────────┬────────────┘                                  │
                      │                                               │
                      ▼                                               │
                ┌───────────────┐                                     │
                │    auth-db     │  PostgreSQL:5435                   │
                └───────────────┘                                     │
                                                                      │ 
    Cliente ── login/register ────────────────────────────────────────┘
    │
    ├─► recibe JWT y llama api-gateway (Authorization: Bearer …)
    │
    └─► api-gateway valida JWT y propaga el header a los servicios

---

## 🔒 Cambios recientes en seguridad

- **Autenticación centralizada** con `auth-service` → Emite JWT firmados con HS256.  
- **Gateway** ahora valida JWT (`issuer`, `secret`) y propaga el token a los microservicios.  
- **Feign** usa **OkHttp** con un `RequestInterceptor` que reenvía el JWT desde el `SecurityContext`.  
- **Ownership** de cuentas por `user_id` (claim `id` en JWT).  
- **Flyway** como único gestor de migraciones
- **Secrets** gestionados vía variables de entorno/Docker secrets (JWT, claves SSH).  

---

## 📂 Servicios

- `auth-service` → Registro/login, emite JWT con claims: `sub=email`, `id=userId`, `roles`.  
- `account-service` → Cuentas vinculadas a `user_id`.  
- `transaction-service` → Idempotencia con `idempotency_key`.  
- `api-gateway` → Valida JWT, enruta y propaga token.  
- `config-server` → Config centralizada (Git).  
- `eureka-server` → Descubrimiento de servicios.  

---

## 🛡️ Recomendaciones de seguridad

1. **Validación estricta de issuer**.  
2. **Políticas de rol** basadas en `roles` del JWT (`ROLE_USER`, `ROLE_ADMIN`).  
3. **Secrets** fuera del repo (`.env`).  

---
## 🔐 Secrets y Config Server (SSH) — **Requerido**

1. **Crear repositorio de configuración en GitHub** (productivo):
  - Crear el repo **`banking-platform-config`** (privado o público, recomendado **privado**).
  - Subir los **tres archivos** (exactos a los de la carpeta local):
    - `account-service.yml`
    - `api-gateway.yml`
    - `transaction-service.yml`

2. **Generar una Deploy Key (SSH)** para el `config-server`:
   ```bash
   ssh-keygen -t rsa -b 4096 -m PEM -N "" -f deploy/secrets/config_server_key
   chmod 600 deploy/secrets/config_server_key
   ```
  - **Subir la pública** `deploy/secrets/config_server_key.pub` a GitHub:
    - Repo **`banking-platform-config`** → *Settings* → *Deploy Keys* → **Add deploy key** → *Read-only*.
  - **No** reutilizar la misma deploy key en varios repos.

3. **Montaje del secreto en Docker Compose**:
  - Ya está configurado para montar `deploy/secrets/config_server_key` en `/run/secrets/config_server_key` dentro del contenedor del `config-server`.
  - El `config-server` usa **Spring ConfigTree** para leer el contenido del archivo y pasarlo a `spring.cloud.config.server.git.privateKey`.

> 💡 Alternativa sin SSH: **HTTPS + Personal Access Token** (PAT). Montá el token como secreto y configura `spring.cloud.config.server.git.username/password`. Recomendado si no querés lidiar con host keys SSH.

---

## Despliegue rápido

1. **Clonar el repo principal**
   ```bash
   git clone https://github.com/tu-usuario/banking-platform.git
   cd banking-platform/deploy
   ```

2. **(Opción A - DEMO local)** Usar la carpeta incluida `banking-platform-config/`
  - No requiere GitHub; el `config-server` puede apuntar a esa carpeta local si así lo configurás (solo para DEMO).

3. **(Opción B - Producción)** Crear el **repo GitHub** `banking-platform-config` y subir:
  - `account-service.yml`, `api-gateway.yml`, `transaction-service.yml`.
  - Crear **Deploy Key** como se explica arriba (o PAT).

4. **Copiar secrets** al equipo destino**:**
  - `deploy/secrets/config_server_key` (clave privada SSH que tenga acceso al repo de configs).
  - Permisos:
    ```bash
    chmod 600 deploy/secrets/config_server_key
    ```

5. **Levantar todo con Docker Compose**
   ```bash
   docker compose up --build -d
   ```

6. **Verificar**
  - Eureka: `http://localhost:8761`
  - Config Server: `http://localhost:8888/account-service/default`
  - Gateway: `http://localhost:8080`

---

**Endpoints útiles**
- Eureka → `http://localhost:8761`  
- Config Server → `http://localhost:8888/account-service/default`  
- Gateway → `http://localhost:8080`  

---

## 📚 Endpoints (EXPLÍCITOS)

### Account Service (`/accounts` — puerto interno 8081, expuesto por el gateway en 8080)

```http
GET /accounts/{id}
POST /accounts
PUT /accounts/transaction
```

**Ejemplos de uso (JSON):**
```http
POST /accounts
Content-Type: application/json

{
  "holderName": "Ada Lovelace",
  "currency": "USD"  // o el enum que uses, p.ej. "USD"
}
```

```http
PUT /accounts/transaction
Content-Type: application/json

{
  "sourceId": 1,
  "targetId": 2,
  "transactionId": 123,       // id de la transacción (del transaction-service)
  "amount": 100.00,
  "currencyCode": "USD"
}
```

### Transaction Service (`/transactions` — puerto interno 8082, expuesto por el gateway en 8080)

```http
POST /transactions
GET  /transactions/{id}
GET  /transactions/source/{id}
GET  /transactions/target/{id}
GET  /transactions          ?sourceId={sid}&targetId={tid}
GET  /transactions/dialog   ?a={idA}&b={idB}
PATCH /transactions/{id}/approve
PATCH /transactions/{id}/reject
```

**Crear transacción (pendiente)**
```http
POST /transactions
Content-Type: application/json

{
  "accountIdSource": 1,
  "accountIdTarget": 2,
  "amount": 100.00,
  "currency": "USD",
  "description": "Pago de servicio",
  "idempotencyKey": "f9f2b5a2-6e4f-4b76-8c6f-1234567890ab"  // opcional, recomendado
}
```

**Aprobar / Rechazar**
```http
PATCH /transactions/{id}/approve
PATCH /transactions/{id}/reject
```

---
## ⚙️ Variables y `.env.example` (sugerido)

Creá `deploy/.env` a partir de este ejemplo:

```dotenv
# ==== DB Accounts ====
ACCOUNT_DB=account_db
ACCOUNT_USER=account_user
ACCOUNT_PASSWORD=123456789
ACCOUNT_DATASOURCE_URL=jdbc:postgresql://accounts-db:5432/${ACCOUNT_DB}
ACCOUNT_DATASOURCE_USERNAME=${ACCOUNT_USER}
ACCOUNT_DATASOURCE_PASSWORD=${ACCOUNT_PASSWORD}

# ==== DB Transactions ====
TRANSACTION_DB=transaction_db
TRANSACTION_USER=transaction_user
TRANSACTION_PASSWORD=123456789
TRANSACTION_DATASOURCE_URL=jdbc:postgresql://transactions-db:5432/${TRANSACTION_DB}
TRANSACTION_DATASOURCE_USERNAME=${TRANSACTION_USER}
TRANSACTION_DATASOURCE_PASSWORD=${TRANSACTION_PASSWORD}

# ==== Otros ====
EUREKA_URI=http://eureka-server:8761/eureka
```



## ❗ Problemas comunes

- **`UnknownHostException` a la DB** → Usar el **nombre del servicio** como host (`accounts-db`, `transactions-db`), no `localhost`.
- **Healthcheck fail del config-server** → Asegurate de exponer `management.endpoints.web.exposure.include=health,info`.
- **SSH fallando**:
  - La privada debe ser **PEM** (`-----BEGIN RSA PRIVATE KEY-----`).
  - Deploy Key cargada **en ese repo** y **read-only**.
  - Podés testear dentro del contenedor si instalás `openssh-client`:
    ```sh
    ssh -i /run/secrets/config_server_key -o IdentitiesOnly=yes -T git@github.com
    ```
- **401 en gateway** → Revisar `JWT_SECRET` y `JWT_ISSUER`.  
- **Migraciones fallidas** → Revisar scripts Flyway y estado de la DB.


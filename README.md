# Banking Platform - Microservices 🏦

Plataforma bancaria basada en **microservicios** con **Spring Boot 3**, **Spring Cloud** (**Config Server, Eureka, Gateway**) y **Docker Compose**.  
Incluye **idempotencia** en transacciones y **ledger contable** para aplicar débitos/créditos en forma segura.

---

## 🔎 Qué hay de nuevo vs. la versión monolítica
- Separación en servicios: `account-service`, `transaction-service`, `api-gateway`, `config-server`, `eureka-server`.
- Configuración **centralizada** en Git con **Spring Cloud Config Server** (vía **SSH** o **HTTPS+PAT**).
- **API Gateway (WebFlux)** para ruteo y descubrimiento vía **Eureka**.
- **Idempotency-Key** en `transaction-service` y aplicación **exacta una vez** (`ON CONFLICT`) en `account-service` mediante `account_ledger`.

---

## 🏗️ Arquitectura

```
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
```

---

## 📂 Estructura del repo

> **Importante:** En este repo se incluye una carpeta **`banking-platform-config/`** con archivos `*.yml` de ejemplo (para demo local).  
> En **producción**, debés crear **otro repositorio Git** con estos mismos archivos y dar acceso al `config-server` (Deploy Key **SSH** o **HTTPS + PAT**).

```bash
.
├── banking-platform-config          # ✔️ Configs centralizadas (demo local)
│   ├── account-service.yml
│   ├── api-gateway.yml
│   └── transaction-service.yml
├── deploy
│   ├── docker-compose.yml
│   └── secrets
│       ├── config_server_key       # 🔒 Clave privada SSH (NO subir a Git)
│       └── config_server_key.pub   # 🔓 Clave pública (se puede subir)
└── services
    ├── account-service
    ├── transaction-service
    ├── api-gateway
    ├── config-server
    └── eureka-server
```

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

## 🧪 Cómo desplegar en **otra computadora** (paso a paso)

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

> No subas `deploy/.env` al repo. Añadido en `.gitignore`.

---

## 🧱 Migraciones (Flyway)

- Cada servicio aplica sus migraciones contra su propia DB (cuentas vs transacciones).
- `account-service` incluye la tabla `account_ledger` con *constraints* para **evitar doble aplicación**:
  - `UNIQUE (transaction_id, account_id, type)`
- `transaction-service` define `idempotency_key UNIQUE` para crear transacciones idempotentes.

---

## 🛡️ Observabilidad y resiliencia (resumen)

- **Retries + Circuit Breaker** en llamadas de `transaction-service` → `account-service`.
- **Idempotencia** en ambos lados para tolerar reintentos.
- **Locks y orden de bloqueo** en `account-service.transaction(...)` para evitar deadlocks.

---

## 🐳 Comandos útiles

```bash
# Ver estado de containers
docker compose ps

# Logs de un servicio
docker compose logs -f config-server

# Probar que el repo de config responde
curl http://localhost:8888/account-service/default

# Probar API vía gateway
curl http://localhost:8080/accounts/1
curl http://localhost:8080/transactions/1
```

---

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
  - Alternativa simple: **HTTPS + PAT**.

---

## 📜 Licencia
MIT (o la que definas).

---

> ¿Necesitás un `README` separado para cada servicio con instrucciones específicas (puertos, perfiles, migraciones)? Decime y lo genero.
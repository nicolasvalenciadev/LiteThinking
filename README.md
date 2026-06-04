# LiteThinking — Sistema de Gestión Empresarial

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Vue.js](https://img.shields.io/badge/Vue.js-3-brightgreen)
![Quasar](https://img.shields.io/badge/Quasar-2.x-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![GCP](https://img.shields.io/badge/GCP-Compute%20Engine-blue)
![Lighthouse](https://img.shields.io/badge/Lighthouse-100%2F100%2F100%2F100-brightgreen)

Sistema web para la gestión de empresas, productos e inventario, desarrollado como solución al reto técnico de LiteThinking Consulting. Implementa una arquitectura de microservicios con Spring Boot, frontend en Vue.js + Quasar Framework y base de datos PostgreSQL.

## 🌐 Aplicación Desplegada

> **https://litethinking.nvvdev.com**

Desplegada en Google Cloud Platform — Compute Engine con Docker Compose.

---

## 📋 Tabla de Contenidos

- [Credenciales de Acceso](#credenciales-de-acceso)
- [Aplicación Desplegada](#-aplicación-desplegada)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Modelo Entidad-Relación](#modelo-entidad-relación)
- [Pruebas Lighthouse](#pruebas-lighthouse)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Ejecución Local con Docker](#ejecución-local-con-docker)
- [Ejecución sin Docker](#ejecución-sin-docker)
- [Variables de Entorno](#variables-de-entorno)
- [Endpoints de la API](#endpoints-de-la-api)
- [Funcionalidades](#funcionalidades)
- [Decisiones Técnicas](#decisiones-técnicas)
- [Pruebas](#pruebas)

---

## 🔑 Credenciales de Acceso

| Perfil | Usuario | Contraseña |
|--------|---------|------------|
| Administrador | `admin` | `Admin123!` |
| Externo | `external` | `External123!` |

---

## 🏗️ Arquitectura

El sistema está compuesto por 5 microservicios independientes más un frontend, todos orquestados mediante Docker Compose:

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (Vue + Quasar)               │
│                      localhost:9000                      │
└───────────────────────────┬─────────────────────────────┘
                            │ HTTP
┌───────────────────────────▼─────────────────────────────┐
│                      API Gateway                         │
│              localhost:8080 — Enrutamiento y JWT         │
└──────┬───────────┬──────────────┬───────────────┬───────┘
       │           │              │               │
┌──────▼──┐  ┌────▼──────┐  ┌───▼──────┐  ┌────▼──────┐
│  auth   │  │  company  │  │ product  │  │ inventory │
│ service │  │  service  │  │ service  │  │  service  │
│  :8081  │  │   :8082   │  │  :8083   │  │   :8084   │
└──────┬──┘  └────┬──────┘  └───┬──────┘  └────┬──────┘
       │          │              │               │
       └──────────┴──────────────┴───────────────┘
                            │
              ┌─────────────▼──────────────┐
              │        PostgreSQL 16        │
              │         localhost:5432      │
              └────────────────────────────┘
```

### Principios Arquitectónicos

- **Arquitectura Hexagonal (Ports & Adapters)** en cada microservicio
- **Principios SOLID** aplicados explícitamente en todas las capas
- **Clean Architecture** con separación estricta de dominio, aplicación e infraestructura
- **Dominio puro** — cero dependencias de frameworks en la capa de dominio

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 (Corretto) | Lenguaje principal |
| Spring Boot | 3.x | Framework de microservicios |
| Spring Security 6 | 6.x | Seguridad y autenticación |
| Spring Cloud Gateway | 2023.x | API Gateway reactivo |
| Spring Data JPA | 3.x | Persistencia |
| Hibernate | 6.x | ORM |
| JWT (jjwt) | 0.12.3 | Tokens de autenticación |
| JasperReports | 6.21.0 | Generación de PDF |
| JavaMail | - | Envío de correos SMTP |
| Maven | 3.9 | Gestión de dependencias |
| Lombok | 1.18.36 | Reducción de boilerplate |

### Frontend
| Tecnología | Versión | Uso |
|---|---|---|
| Vue.js | 3 | Framework frontend |
| Quasar Framework | 2.x | Componentes UI Material Design |
| Pinia | - | Gestión de estado |
| Vue Router | 4 | Enrutamiento |
| Axios | - | Cliente HTTP |
| Vite | - | Bundler |

### Infraestructura
| Tecnología | Uso |
|---|---|
| PostgreSQL 16 | Base de datos principal |
| Docker + Docker Compose | Contenedorización y orquestación |
| Nginx | Servidor web del frontend |

---

## 🗄️ Modelo Entidad-Relación

### Diagrama

![Modelo Entidad-Relación](docs/MER%20-%20litethinking.png)



```
roles ──────────────── users ──────────── clients
  │                                          │
  │                                          │
companies                                 orders ──── status
  │                                          │
  │                                          │
products ──── product_prices          order_product
  │
  │
category_product ──── categories
```

### Tablas

| Tabla | Descripción |
|---|---|
| `roles` | Roles del sistema: ADMIN, EXTERNAL |
| `users` | Usuarios con credenciales encriptadas (BCrypt) |
| `clients` | Clientes del sistema |
| `companies` | Empresas (NIT, nombre, dirección, teléfono) |
| `products` | Productos por empresa (código, nombre, descripción) |
| `product_prices` | Precios multi-moneda por producto (COP, USD, EUR) |
| `categories` | Categorías de productos |
| `category_product` | Relación M:N productos ↔ categorías |
| `orders` | Órdenes de clientes |
| `order_product` | Relación M:N órdenes ↔ productos |
| `status` | Estados de órdenes (PENDING, CONFIRMED, DELIVERED, CANCELLED) |

Todas las tablas incluyen:
- `id` (UUID) como llave primaria
- `created_date` y `last_update` para auditoría
- `deleted` (BOOLEAN) para soft delete

---

## 🔦 Pruebas Lighthouse

Auditoría de calidad del frontend realizada con Google Lighthouse sobre la aplicación desplegada en producción.

### Desktop — 100/100/100/100

![Lighthouse Desktop](docs/Lighthouse%20Desktop.png)

| Métrica | Score |
|---|---|
| Performance | ✅ 100 |
| Accessibility | ✅ 100 |
| Best Practices | ✅ 100 |
| SEO | ✅ 100 |

### Mobile — 95/100/100/100

![Lighthouse Mobile](docs/Lighthouse%20Mobile.png)

| Métrica | Score |
|---|---|
| Performance | ✅ 95 |
| Accessibility | ✅ 100 |
| Best Practices | ✅ 100 |
| SEO | ✅ 100 |

---

## 📁 Estructura del Proyecto

```
LiteThinking/
├── backend/
│   ├── api-gateway/              # Enrutamiento y validación JWT
│   ├── auth-service/             # Autenticación y generación de tokens
│   ├── company-service/          # CRUD de empresas
│   ├── product-service/          # CRUD de productos y categorías
│   └── inventory-service/        # Inventario, PDF y correo
├── frontend/
│   └── litethinking-ui/          # Vue 3 + Quasar
├── database/
│   ├── init.sql                  # DDL — creación de tablas
│   └── seed.sql                  # Datos iniciales del sistema
├── docker-compose.yml
└── README.md
```

### Estructura por Microservicio (Arquitectura Hexagonal)

```
{service}/src/main/java/com/litethinking/{service}/
├── domain/
│   ├── model/          # Modelos de dominio (Java puro, sin frameworks)
│   ├── exception/      # Excepciones de dominio
│   └── port/
│       ├── in/         # Casos de uso (interfaces de entrada)
│       └── out/        # Puertos de salida (interfaces de repositorio)
├── application/
│   └── service/        # Implementación de casos de uso
└── infrastructure/
    ├── config/         # Configuración de Spring
    ├── persistence/    # Entidades JPA, repositorios y adaptadores
    ├── web/            # Controladores, DTOs y manejo de excepciones
    └── mapper/         # Mapeo entre capas
```

---

## ✅ Requisitos Previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) 24.x o superior
- [Docker Compose](https://docs.docker.com/compose/) (incluido en Docker Desktop)
- Git

Para desarrollo local sin Docker:
- Java 17 (Amazon Corretto recomendado)
- Maven 3.9+
- Node.js 22+
- PostgreSQL 16

---

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd LiteThinking
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto o exporta las variables:

```bash
export MAIL_USERNAME=tu-correo@gmail.com
export MAIL_PASSWORD=tu-app-password-gmail
```

> **Nota:** Para `MAIL_PASSWORD` usa una App Password de Gmail, no tu contraseña real.
> Puedes generarla en [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)

---

## 🐳 Ejecución Local con Docker

### Levantar todos los servicios

```bash
docker compose up --build -d
```

### Verificar que todos los contenedores estén corriendo

```bash
docker compose ps
```

Deberías ver 7 contenedores en estado `Up`:

| Contenedor | Puerto | Descripción |
|---|---|---|
| litethinking-db | 5432 | PostgreSQL |
| litethinking-auth | 8081 | Auth Service |
| litethinking-company | 8082 | Company Service |
| litethinking-product | 8083 | Product Service |
| litethinking-inventory | 8084 | Inventory Service |
| litethinking-gateway | 8080 | API Gateway |
| litethinking-frontend | 9000 | Frontend Vue/Quasar |

### Acceder a la aplicación

Abre el navegador en: **http://localhost:9000**

### Ver logs de un servicio específico

```bash
docker logs litethinking-auth -f
docker logs litethinking-gateway -f
```

### Detener todos los servicios

```bash
docker compose down
```

### Detener y eliminar volúmenes (reset completo)

```bash
docker compose down -v
```

---

## 💻 Ejecución sin Docker

### 1. Levantar solo la base de datos

```bash
docker compose up -d postgres
```

### 2. Ejecutar cada microservicio

Desde cada carpeta de servicio:

```bash
# auth-service
cd backend/auth-service
export DB_HOST=localhost DB_PORT=5432 DB_NAME=litethinking_db
export DB_USER=litethinking_user DB_PASSWORD=litethinking_pass
export JWT_SECRET=bXlTdXBlclNlY3JldEtleUZvckxpdGVUaGlua2luZ1Byb2plY3QyMDI0ISFAQCMkJV4mKg==
mvn spring-boot:run

# company-service (nueva terminal)
cd backend/company-service
export DB_HOST=localhost DB_PORT=5432 DB_NAME=litethinking_db
export DB_USER=litethinking_user DB_PASSWORD=litethinking_pass
mvn spring-boot:run

# product-service (nueva terminal)
cd backend/product-service
export DB_HOST=localhost DB_PORT=5432 DB_NAME=litethinking_db
export DB_USER=litethinking_user DB_PASSWORD=litethinking_pass
mvn spring-boot:run

# inventory-service (nueva terminal)
cd backend/inventory-service
export DB_HOST=localhost DB_PORT=5432 DB_NAME=litethinking_db
export DB_USER=litethinking_user DB_PASSWORD=litethinking_pass
export MAIL_USERNAME=tu-correo@gmail.com
export MAIL_PASSWORD=tu-app-password
mvn spring-boot:run

# api-gateway (nueva terminal)
cd backend/api-gateway
export JWT_SECRET=bXlTdXBlclNlY3JldEtleUZvckxpdGVUaGlua2luZ1Byb2plY3QyMDI0ISFAQCMkJV4mKg==
mvn spring-boot:run
```

### 3. Ejecutar el frontend

```bash
cd frontend/litethinking-ui
npm install
npm run dev
```

Accede en: **http://localhost:9000**

---

## 🔧 Variables de Entorno

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `litethinking_db` |
| `DB_USER` | Usuario de PostgreSQL | `litethinking_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `litethinking_pass` |
| `JWT_SECRET` | Secret para firmar JWT (Base64, mín. 256 bits) | — |
| `MAIL_USERNAME` | Correo Gmail para envío | — |
| `MAIL_PASSWORD` | App Password de Gmail | — |
| `AUTH_SERVICE_URL` | URL del auth-service | `http://localhost:8081` |
| `COMPANY_SERVICE_URL` | URL del company-service | `http://localhost:8082` |
| `PRODUCT_SERVICE_URL` | URL del product-service | `http://localhost:8083` |
| `INVENTORY_SERVICE_URL` | URL del inventory-service | `http://localhost:8084` |

---

## 🌐 Endpoints de la API

Todos los endpoints se consumen a través del API Gateway en `http://localhost:8080`.

### Autenticación (Público)

```
POST /api/auth/login
Body: { "username": "admin", "password": "Admin123!" }
Response: { "token": "...", "role": "ADMIN", "username": "admin" }
```

### Empresas

```
GET    /api/companies          # Listar empresas (ADMIN + EXTERNAL)
GET    /api/companies/{id}     # Obtener empresa (ADMIN + EXTERNAL)
POST   /api/companies          # Crear empresa (ADMIN)
PUT    /api/companies/{id}     # Actualizar empresa (ADMIN)
DELETE /api/companies/{id}     # Eliminar empresa — soft delete (ADMIN)
```

### Productos

```
GET    /api/products                     # Listar productos (ADMIN + EXTERNAL)
GET    /api/products/{id}                # Obtener producto (ADMIN + EXTERNAL)
GET    /api/products/company/{companyId} # Productos por empresa (ADMIN + EXTERNAL)
POST   /api/products                     # Crear producto (ADMIN)
PUT    /api/products/{id}                # Actualizar producto (ADMIN)
DELETE /api/products/{id}                # Eliminar producto — soft delete (ADMIN)
```

### Categorías

```
GET    /api/categories       # Listar categorías
GET    /api/categories/{id}  # Obtener categoría
POST   /api/categories       # Crear categoría (ADMIN)
PUT    /api/categories/{id}  # Actualizar categoría (ADMIN)
DELETE /api/categories/{id}  # Eliminar categoría (ADMIN)
```

### Inventario

```
GET  /api/inventory              # Ver inventario (ADMIN)
GET  /api/inventory/pdf          # Descargar PDF (ADMIN)
POST /api/inventory/send-email   # Enviar PDF por correo (ADMIN)
     Body: { "email": "destinatario@example.com" }
```

### Headers requeridos (rutas protegidas)

```
Authorization: Bearer <token>
```

---

## ⚙️ Funcionalidades

### Vista de Login
- Formulario con usuario y contraseña
- Autenticación JWT con Spring Security
- Contraseñas encriptadas con BCrypt
- Redirección automática según rol

### Vista de Empresas
- **ADMIN:** Crear, editar y eliminar empresas (soft delete)
- **EXTERNAL:** Solo visualización
- Campos: NIT (único), nombre, dirección, teléfono

### Vista de Productos
- **ADMIN:** CRUD completo de productos
- Precios en múltiples monedas: COP, USD, EUR
- Asignación de categorías múltiples por producto
- Asociación con empresa

### Vista de Inventario
- Tabla de productos agrupados por empresa
- **Descarga de PDF** generado con JasperReports
- **Envío por correo** del PDF via API SMTP Gmail
- Acceso exclusivo para ADMIN

### Seguridad
- JWT con firma HMAC-SHA384
- Tokens con expiración de 24 horas
- Validación centralizada en el API Gateway
- Headers enriquecidos hacia microservicios (`X-User-Role`, `X-Username`, `X-User-Id`)
- Soft delete en todas las entidades

---

## 🏛️ Decisiones Técnicas

### Arquitectura Hexagonal
Cada microservicio implementa Ports & Adapters para aislar el dominio de los detalles de infraestructura. El dominio no tiene dependencias de Spring, JPA ni ningún framework.

### JWT sin Keycloak
Se optó por JWT nativo con Spring Security 6 dado el tiempo disponible. El API Gateway valida los tokens y enriquece las requests con headers, eliminando la necesidad de validar JWT en cada microservicio.

### Reactor/WebFlux en el Gateway
El API Gateway usa Spring Cloud Gateway con WebFlux (reactivo, no bloqueante) para manejar alta concurrencia sin bloquear hilos. Los microservicios downstream usan Spring MVC tradicional.

### `Mono<Boolean>` en puertos del Gateway
Se tomó la decisión pragmática de permitir `Mono` en los puertos del dominio del gateway, dado que toda la aplicación es reactiva de punta a punta. `Mono` actúa como tipo funcional más que como dependencia de framework.

### Soft Delete con `@SQLRestriction`
Todas las entidades usan `@SQLRestriction("deleted = false")` de Hibernate 6 para filtrar automáticamente registros eliminados en todas las consultas, sin modificar los repositorios.

### UUID como Primary Key
Todas las tablas usan UUID como llave primaria para facilitar la distribución, evitar colisiones y no exponer secuencias predecibles en la API.

### Precios Multi-moneda
La tabla `product_prices` con la restricción `UNIQUE(product_id, currency)` garantiza un precio por moneda por producto, permitiendo agregar nuevas monedas sin modificar el esquema.

### PDF en Disco + Adjunto en Email
El PDF se genera con JasperReports, se guarda en el directorio temporal del sistema y se adjunta al correo vía JavaMail. Esto permite tanto la descarga directa como el envío por correo sin regenerar el archivo.

---

## 🧪 Pruebas

### Ejecutar pruebas de todos los servicios

```bash
# auth-service — 14 tests
cd backend/auth-service && mvn test

# api-gateway — 6 tests
cd backend/api-gateway && mvn test

# company-service — 11 tests
cd backend/company-service && mvn test

# product-service — 13 tests
cd backend/product-service && mvn test

# inventory-service — 12 tests
cd backend/inventory-service && mvn test
```

**Total: 56 tests unitarios — 0 fallos**

### Cobertura de pruebas

Cada servicio incluye:
- Pruebas unitarias de la capa de aplicación (servicios) con Mockito
- Pruebas de controladores con `@WebMvcTest` y MockMvc
- Mocks sobre interfaces/puertos, nunca sobre clases concretas

### Prueba end-to-end con curl

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123!"}' | \
  python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

# 2. Listar empresas
curl -s http://localhost:8080/api/companies \
  -H "Authorization: Bearer $TOKEN" | json_pp

# 3. Ver inventario
curl -s http://localhost:8080/api/inventory \
  -H "Authorization: Bearer $TOKEN" | json_pp

# 4. Descargar PDF
curl -s http://localhost:8080/api/inventory/pdf \
  -H "Authorization: Bearer $TOKEN" \
  --output inventario.pdf

# 5. Enviar PDF por correo
curl -s -X POST http://localhost:8080/api/inventory/send-email \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email":"destinatario@example.com"}'
```

---

## 📝 Consideraciones Técnicas

- El frontend corre en modo SPA servido por Nginx dentro de Docker
- El `nginx.conf` actúa como proxy reverso hacia el API Gateway para las llamadas `/api/*`
- Las credenciales de Gmail se manejan exclusivamente como variables de entorno, nunca en código fuente
- El JWT Secret debe ser un string codificado en Base64 de mínimo 256 bits
- Los triggers de PostgreSQL actualizan automáticamente `last_update` en cada modificación
- Los índices parciales (`WHERE deleted = false`) optimizan las consultas de registros activos

---

*Desarrollado por Nicolas Valencia — LiteThinking Technical Challenge 2026*

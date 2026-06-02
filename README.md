# Organizapp Backend

Backend del sistema de gestión y modelado de procesos BPMN multiempresa.

## Arquitectura de seguridad

```
┌──────────┐     POST /api/auth/login      ┌───────────────┐
│  Client  │ ──────────────────────────────>│ AuthController │
│(Frontend)│ <─── JWT (sub, companyId, rol) │               │
└──────────┘                                └───────────────┘
     │
     │  Authorization: Bearer <token>
     ▼
┌──────────────┐    valida firma,    ┌─────────────────┐
│ JwtAuthFilter│─── issuer, exp  ───>│ SecurityContext  │
│              │    extrae claims    │  (authorities)   │
└──────────────┘                     └─────────────────┘
     │
     ▼
┌──────────────────────────────────────────────┐
│  @PreAuthorize + Tenant Isolation            │
│  Cada query filtrada por companyId del JWT   │
└──────────────────────────────────────────────┘
```

## Tecnologías

- Java 17 / Spring Boot 4.x
- Spring Security + JWT (jjwt 0.12.3)
- PostgreSQL
- Docker (multi-stage build)
- GitHub Actions CI + SonarQube

## Variables de entorno requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/organizapp` |
| `DB_USER` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | `secret` |
| `JWT_SECRET` | Clave HMAC para firmar JWT (mínimo 64 caracteres) | `MiClaveSecretaMuyLarga...` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos por CORS | `http://localhost:4200` |

## Ejecutar con Docker Compose

```bash
# Crea un archivo .env con las variables de entorno (o expórtalas manualmente)
docker compose up --build
```

## Ejecutar en local

```bash
# Requiere PostgreSQL corriendo en localhost:5432
export DB_URL=jdbc:postgresql://localhost:5432/organizapp
export DB_USER=postgres
export DB_PASSWORD=secret
export JWT_SECRET=ClaveDesarrolloLocal1234567890AbCdEfGhIjKlMnOpQrStUvWxYz
mvn spring-boot:run
```

## Documentación de la API

Con la aplicación corriendo, accede a Swagger UI en:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Endpoints principales

| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| POST | `/api/auth/login` | Público | Login, retorna JWT |
| POST | `/api/companies/register` | Público | Registro de nueva empresa |
| GET | `/api/processes` | Autenticado | Procesos de la empresa del usuario |
| POST | `/api/processes` | Autenticado | Crear proceso |
| GET | `/api/roles` | Autenticado | Roles de la empresa |
| POST | `/api/roles` | Solo ADMIN | Crear rol |
| GET | `/actuator/health` | Público | Health check |

## Modelo de seguridad

- **Autenticación**: JWT firmado con HMAC-SHA. Claims: `sub` (email), `userId`, `companyId`, `rolNombre`, `iss`, `iat`, `exp`
- **Autorización por roles**: `@PreAuthorize` con roles `ADMIN` y `USER`
  - `ADMIN`: gestión de usuarios, roles, permisos, empresas
  - `USER`: gestión de procesos BPMN (crear, editar, versionar)
- **Aislamiento multiempresa (tenant isolation)**: cada query filtra por `companyId` extraído del JWT — un usuario de empresa A nunca accede a datos de empresa B
- **Passwords**: BCrypt
- **Métricas**: Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`)

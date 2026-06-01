# 🏎️ Limando Carter Rfactor (LCR)

Plataforma web fullstack para compartir y gestionar mods de simuladores de carreras (rFactor, Assetto Corsa). Permite a los usuarios explorar publicaciones por categoría, buscar mods, y a los administradores gestionar el contenido de la plataforma.

---

## 🛠️ Stack tecnológico

### Frontend
| Tecnología | Uso |
|---|---|
| React 18 | Librería principal de UI |
| Vite | Bundler y entorno de desarrollo |
| React Router DOM v6 | Enrutado SPA |
| React Bootstrap 5 | Componentes de UI |
| jwt-decode | Decodificación del token en cliente |

### Backend
| Tecnología | Uso |
|---|---|
| Java + Spring Boot | Framework principal |
| Spring Security | Autenticación y autorización |
| JWT (jjwt) | Tokens de sesión stateless |
| Spring Data JPA | Persistencia de datos |
| BCrypt | Hash de contraseñas |
| Lombok | Reducción de boilerplate |

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────┐
│           Frontend              │
│  React + Vite (puerto 5173)     │
│                                 │
│  /src                           │
│  ├── Components/                │
│  │   ├── Formularios/           │
│  │   ├── Publicaciones/         │
│  │   └── Secundarios/           │
│  └── Service/                   │
│      ├── UserService.js         │
│      ├── publicacionService.js  │
│      └── ServicioAdmin.js       │
└────────────┬────────────────────┘
             │ HTTP + Bearer JWT
┌────────────▼────────────────────┐
│           Backend               │
│  Spring Boot (puerto 8080)      │
│                                 │
│  /auth/**        → público      │
│  /imagen/**      → público      │
│  /publicacion/** → público      │
│  /publicacion/editar/**   ┐     │
│  /publicacion/eliminar/** ├ADMIN│
│  /publicacion/destacadaA/**     │
│  /publicacion/destacadaE/**┘    │
└────────────┬────────────────────┘
             │ JPA
┌────────────▼────────────────────┐
│           Base de datos         │
│           (MySQL / H2)          │
└─────────────────────────────────┘
```

### Flujo de autenticación

```
Usuario → POST /auth/login
        ← JWT token (claims: sub, userNombre, userRol, userId)

Requests protegidos → Authorization: Bearer <token>
                     → JwtAuthenticationFilter valida firma y expiración
                     → Spring Security verifica rol en SecurityFilterChain
```

---

## 🔐 Sistema de roles

| Rol | Permisos |
|---|---|
| `USER` | Ver publicaciones, buscar, filtrar por categoría |
| `ADMIN` | Todo lo anterior + subir, editar, eliminar y destacar publicaciones |

El rol se asigna automáticamente como `USER` al registrarse. Para promover un usuario a `ADMIN` se hace directamente en la base de datos.

---

## 📁 Estructura del proyecto

```
lcr/
├── api-lcr/                  # Frontend React
│   ├── src/
│   │   ├── Components/
│   │   │   ├── Formularios/  # Login, Registro, Subir/Editar publicación
│   │   │   ├── Publicaciones/# Listado, detalle, búsqueda, por categoría
│   │   │   └── Secundarios/  # Card, ListaCards, Loader, Producto
│   │   ├── Service/          # Lógica de llamadas a la API
│   │   └── App.jsx           # Rutas y layout principal
│   └── vite.config.js
│
└── v1/                       # Backend Spring Boot
    ├── Auth/                 # Login, registro, DTOs
    ├── Controladores/        # Publicacion, Imagen
    ├── Entidades/            # User, Publicacion, Imagen
    ├── Enums/                # Role, Categoria
    ├── Jwt/                  # Filter y Service de JWT
    ├── Repositorios/         # JPA Repositories
    ├── Servicios/            # Lógica de negocio
    └── SecurityConfig.java   # Configuración de Spring Security
```

---

## 🚀 Cómo levantar el proyecto localmente

### Requisitos previos
- Java 17+
- Node.js 18+
- MySQL (o H2 para desarrollo)
- Maven

### Backend

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/lcr.git
cd lcr/v1

# 2. Configurar las variables en application.properties
# jwt.secret=<tu_clave_base64>
# spring.datasource.url=jdbc:mysql://localhost:3306/lcr
# spring.datasource.username=<usuario>
# spring.datasource.password=<password>

# 3. Correr
mvn spring-boot:run
```

### Frontend

```bash
cd lcr/api-lcr

# Instalar dependencias
npm install

# Levantar en modo desarrollo
npm run dev
```

La app estará disponible en `http://localhost:5173` y consumirá el backend en `http://localhost:8080`.

---

## 📌 Endpoints principales

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/register` | Público | Registro de usuario |
| POST | `/auth/login` | Público | Login, devuelve JWT |
| GET | `/publicacion` | Público | Publicaciones destacadas |
| GET | `/publicacion/all` | Público | Todas las publicaciones |
| GET | `/publicacion/one/{id}` | Público | Detalle de publicación |
| GET | `/publicacion/categoria/{cat}` | Público | Filtrar por categoría |
| GET | `/publicacion/buscar/{consulta}` | Público | Búsqueda |
| POST | `/publicacion` | ADMIN | Subir publicación |
| POST | `/publicacion/editar/{id}` | ADMIN | Editar publicación |
| POST | `/publicacion/eliminar/{id}` | ADMIN | Eliminar publicación |
| POST | `/publicacion/destacadaA/{id}` | ADMIN | Marcar como destacada |
| POST | `/publicacion/destacadaE/{id}` | ADMIN | Quitar de destacadas |
| GET | `/imagen/{id}` | Público | Obtener imagen |

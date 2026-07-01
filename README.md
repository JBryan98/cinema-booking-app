# Proyecto final cinema-booking-app

Proyecto final del curso de microservicios con **Spring Boot 4**, **Java 25** y **Spring Cloud 2025.1.0**.

Caso de negocio: Plataforma de reserva de cine donde clientes reservan asientos para una función.

---

## Stack Tecnológico

| Tecnología   | Versión     | Uso                        |
|--------------|-------------|----------------------------|
| Java         | 25          | Lenguaje base              |
| Spring Boot  | 4.1.0       | Framework principal        |
| Spring Cloud | 2025.1.0    | Config, Discovery, Gateway |
| Maven        | 3.9+        | Build tool                 |
| PostgreSQL   | 18          | Base de datos relacional   |
| Apache Kafka | 3.7 (KRaft) | Mensajería y eventos       |

---

## Servicios

| Servicio                       | Puerto | Descripción                      |
|--------------------------------|--------|----------------------------------|
| `api-gateway`                  | 51000  | Api Gateway de la aplicación     |
| `auth-service`                 | 50003  | Servicio de autenticación JWT    |
| `movie-service`                | 51100  | Gestión de películas y funciones |
| `booking-service`              | 51200  | Gestión de reservas              |
| `booking-orchestrator-service` | 51400  | Orquestador SAGA                 |

---

## Módulos cubiertos del Curso

| #  | Módulo                         | Tag                         |
|----|--------------------------------|-----------------------------|
| 02 | Persistencia (Spring Data JPA) | `modulo-02/persistencia`    |
| 03 | DDD + Diseño del Dominio       | `modulo-03/ddd`             |
| 04 | SAGA Happy Path                | `modulo-04/saga-happy-path` |
| 05 | Comunicación HTTP              | `modulo-05/comunicacion`    |
| 08 | Resiliencia                    | `modulo-08/resiliencia`     |
| 09 | Kafka + EDA                    | `modulo-09/kafka-eda`       |
| 12 | Seguridad                      | `modulo-12/seguridad`       |
| 13 | SAGA completo                  | `modulo-13/cqrs-saga`       |

---

--
## Bases de datos

| Contenedor           | Puerto | Descripción                        |
|----------------------|--------|------------------------------------|
| `movie-service-db`   | 5432   | Base de datos de `movie-service`   |
| `booking-service-db` | 5433   | Base de datos de `booking-service` |

- `movie-service` y `booking-service` cuentan con su propia base de datos PostgreSQL `moviedb` y `bookingdb` respectivamente. Las migraciones se gestionan con Flyway.
- Las credenciales de acceso a la base de datos son:
  - Usuario: `admin`
  - Contraseña: `admin123`

## Resiliencia
- `booking-orchestrator-service` Se implementó Retry con CircuitBreaker en el SAGA para el llamado a movie-service
en el endpoint `/screenings/{id}/reserve`.
- Para probarlo utilizar `chaos` implementado en `movie-service`. Los endpoints de `chaos` pueden encontrarse en la
colección de `Postman`. Por practicidad se recomienda asignar un error rate de 0.90.
- Endpoint para asignar el error rate: http://localhost:51100/api/v1/chaos/error

---
## Documentación de la API
La documentación OpenAPI de todos los microservicios se encuentra centralizada a través del API Gateway.
Accede a la interfaz de Swagger desde:

- http://localhost:51000/swagger-ui/index.html

Desde la interfaz podrás seleccionar el microservicio que deseas consultar:

- Movie Service
- Booking Service
- Booking Orchestrator Service
- Auth Service

### Endpoints OpenAPI

Si necesitas acceder directamente al documento OpenAPI (JSON) de cada servicio, el API Gateway los expone en las siguientes rutas:
- `GET /movie-service/api-docs`
- `GET /booking-service/api-docs`
- `GET /booking-orchestrator-service/api-docs`
- `GET /auth-service/api-docs`


## Requisitos Previos

- Java 25 (`java -version`)
- Maven 3.9+ (`mvn -version`)
- Docker + Docker Compose (`docker -v`)

---

## Cómo Levantar

### 1. Infraestructura (Docker)

```bash
make infra-up
```

> Si es la primera vez o se agregaron nuevas bases de datos: `make infra-clean && make infra-up`

### 2. Servicios (terminal)

Cada servicio es independiente:

```bash
mvn spring-boot:run -f booking-service/pom.xml
mvn spring-boot:run -f movie-service/pom.xml
mvn spring-boot:run -f auth-service/pom.xml
mvn spring-boot:run -f booking-orchestrator-service/pom.xml
```

## Arquitectura Hexagonal (Ports & Adapters)

Todos los servicios de negocio siguen la misma estructura de paquetes. El dominio es el núcleo — no depende de Spring,
JPA ni ningún framework.

### Estructura de un servicio de negocio

```
{service}/src/main/java/com/jbryan98/bookingapp/{bounded-context}/
│
├── api/                              ← Adaptador de entrada (HTTP)
│   ├── {Entity}Controller.java       ← @RestController
│   └── dto/
│       ├── {Entity}Request.java      ← body de entrada (validado con Bean Validation)
│       ├── {Entity}UpdateRequest.java
│       └── {Entity}Response.java     ← body de salida
│
├── application/                      ← Puerto de entrada (casos de uso)
│   ├── {Entity}Service.java          ← interfaz — el controlador solo habla aquí
│   └── impl/
│       └── {Entity}ServiceImpl.java  ← implementación — orquesta dominio + repo
│
├── domain/                           ← Núcleo del dominio (sin dependencias de framework)
│   ├── {Entity}.java                 ← entidad rica con métodos de negocio
│   ├── {EnumName}.java               ← enums de dominio
│   ├── {Entity}Repository.java       ← puerto de salida (interfaz pura, sin Spring)
│   └── event/
│       └── {Entity}CreatedEvent.java ← eventos de dominio (placeholder para Kafka)
│
├── exception/                        ← Manejo de errores
│   ├── {Entity}NotFoundException.java
│   └── GlobalExceptionHandler.java   ← RFC 7807 ProblemDetail
│
└── infrastructure/                   ← Adaptadores de salida
    ├── config/
    │   ├── JpaConfig.java            ← Flyway bean
    └── persistence/
        └── Jpa{Entity}Repository.java ← extends JpaRepository + {Entity}Repository
```

### Ejemplo: `booking-orchestrator-service` (stateless)

El orquestador no tiene dominio ni persistencia propia. Su infraestructura son los clientes HTTP a los servicios
downstream.

```
orchestrator/
├── api/
│   ├── BookingPlacementController.java  ← POST /booking-placements → 201 Created
│   └── dto/
│       ├── PlaceBookingRequest.java
│       └── BookingPlacementResponse.java
├── application/
│   ├── SagaOrchestrator.java          ← interfaz del puerto de entrada
│   └── impl/SagaOrchestratorImpl.java ← los 4 pasos del SAGA
├── exception/
│   ├── SagaException.java
│   └── GlobalExceptionHandler.java
└── infrastructure/
    ├── config/
    │   └── ServiceUrlsProperties.java ← @ConfigurationProperties("services")
    └── client/
        ├── BookingServiceClient.java
        ├── MovieServiceClient.java
        └── dto/                       ← copias locales de DTOs de servicios remotos
```

### Reglas de dependencia (Dependency Rule)

```
api → application → domain ← infrastructure
```

- `domain` no importa nada de Spring, JPA ni ningún framework
- `api` y `infrastructure` dependen de `domain`, nunca al revés
- `application` implementa los puertos del dominio usando `domain` y el `repository`

---

## Estructura del Proyecto

```
spring-boot-cloud/
├── docker/                     ← infraestructura Docker
├── postman/                    ← colección Postman
├── api-gateway/                
├── movie-service/
├── booking-service/
├── booking-orchestrator-service/
├── auth-service/
```

---

## Colección de Pruebas

Importar en Postman: `postman/spring-boot-cloud.postman_collection.json`

Las variables de colección ya están configuradas con los valores por defecto:

| Variable                 | Valor por defecto        |
|--------------------------|--------------------------|
| `apiGatewayUrl`          | `http://localhost:51000` |
| `movieServiceUrl`        | `http://localhost:51100` |
| `bookingServiceUrl`      | `http://localhost:51200` |
| `bookingOrchestratorUrl` | `http://localhost:51400` |
| `authServiceUrl`         | `http://localhost:50003` |


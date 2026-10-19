# ms-rutaexpress-catalog

Microservicio de gestión de servicios de envío y capacidad de flota para la plataforma RutaExpress.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- H2 (desarrollo local) / Oracle RDS (producción)
- Maven
- Docker / Docker Compose

## Arquitectura

El proyecto sigue **arquitectura hexagonal** (Ports & Adapters) combinada con **DDD (Domain-Driven Design)**:

src/main/java/com/rutaexpress/catalog/
├── domain/ ← Modelo de dominio puro (sin dependencias de framework)
│ ├── model/ ← Entidades y Value Objects
│ ├── port/in/ ← Puertos de entrada (casos de uso)
│ ├── port/out/ ← Puertos de salida (persistencia, mensajería)
│ └── exception/ ← Excepciones de dominio
├── application/ ← Casos de uso y DTOs
│ ├── dto/ ← Commands y Responses
│ └── service/ ← Implementación de los casos de uso
├── adapter/ ← Adaptadores (REST, JPA, mensajería)
│ ├── in/web/ ← Controladores REST y DTOs de entrada
│ └── out/persistence/ ← Entidades JPA, mappers y adaptadores de persistencia
└── infrastructure/ ← Configuración de Spring


## Endpoints

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/catalog/services` | Crear un nuevo servicio |
| `GET` | `/api/catalog/services` | Listar todos los servicios |
| `GET` | `/api/catalog/services/active` | Listar solo los servicios activos |
| `GET` | `/api/catalog/services/{id}` | Obtener un servicio por ID |
| `PUT` | `/api/catalog/services/{id}` | Actualizar tarifa y/o capacidad |
| `PATCH` | `/api/catalog/services/{id}/activate` | Activar un servicio |
| `PATCH` | `/api/catalog/services/{id}/deactivate` | Desactivar un servicio |
| `DELETE` | `/api/catalog/services/{id}` | Eliminar un servicio |

## Ejemplo de uso

### Crear un servicio

```bash
curl -X POST http://localhost:8081/api/catalog/services \
  -H "Content-Type: application/json" \
  -d '{"name":"Envio Express","basePrice":4990,"currency":"CLP","initialCapacity":10}'
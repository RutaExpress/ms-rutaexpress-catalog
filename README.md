# ms-rutaexpress-catalog

Microservicio de **catálogo** del caso RutaExpress: tipos de envío, tarifa y capacidad disponible. Guarda en **Oracle**.

> Arquitectura en capas (refactor desde hexagonal, rama `refactor/ArquitecturaCapas`).

## Tecnologías
Java 21 · Spring Boot 4.1.2 · Spring Data JPA (Oracle) · Bean Validation · Lombok · JUnit 5 y Mockito

## Arquitectura en capas
Una petición baja siempre en el mismo orden: **controller → service → repository → model**.

```
src/main/java/com/rutaexpress/catalog/
├── controller/   Solo HTTP: recibe el DTO con @Valid, llama al service y responde
├── service/      Reglas del catálogo
├── repository/   Interfaces de Spring Data (JpaRepository): solo acceso a datos
├── model/        Entidad ShippingService (@Entity) y Money (precio y moneda)
├── dto/          Records de entrada y salida, con sus validaciones
└── exception/    Excepciones de negocio y el manejador global (ProblemDetail)
```

## Endpoints (puerto 8083)
| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/v1/catalog/services` | Crear servicio (201; nombre repetido → 409) |
| GET | `/api/v1/catalog/services` | Listar servicios |
| GET | `/api/v1/catalog/services/active` | Listar solo los activos |
| GET | `/api/v1/catalog/services/{id}` | Ver un servicio |
| PUT | `/api/v1/catalog/services/{id}` | Cambiar tarifa y/o capacidad (`capacityDelta`; bajo 0 → 409) |
| PATCH | `/api/v1/catalog/services/{id}/activate` · `/deactivate` | Activar o desactivar |
| DELETE | `/api/v1/catalog/services/{id}` | Eliminar (204) |

## Tests
```
mvnw.cmd test
```

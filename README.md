# Shipment Service

Microservicio REST para la gestión de envíos (ABM completo) construido con Spring Boot 3.5 y Java 21.

## Cómo levantar

Requiere Java 21 y Maven instalados.

    mvn spring-boot:run

La base de datos H2 se crea en memoria al iniciar y se destruye al detener la aplicación.

## URLs útiles

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

H2 Console: JDBC URL jdbc:h2:mem:shipmentdb, usuario sa, sin contraseña.

## Endpoints

| Método | Path | Descripción |
|---|---|---|
| POST | /api/shipments | Crear envío |
| GET | /api/shipments | Listar con filtros opcionales: status, type, destinationCity |
| GET | /api/shipments/{id} | Buscar por ID |
| GET | /api/shipments/tracking/{trackingCode} | Buscar por código de seguimiento |
| PUT | /api/shipments/{id} | Actualizar datos del envío |
| DELETE | /api/shipments/{id} | Baja lógica, cambia estado a CANCELADO |
| PATCH | /api/shipments/{id}/status | Avanzar estado según transiciones válidas |
| GET | /api/shipments/report | Resumen de envíos agrupado por estado |

## Transiciones de estado válidas

PENDIENTE a EN_TRANSITO
PENDIENTE a CANCELADO
EN_TRANSITO a ENTREGADO

Cualquier otra transición devuelve 409 Conflict con mensaje descriptivo.

## Decisiones de diseño

**Virtual Threads (Java 21 / Project Loom)**
Habilitados con spring.threads.virtual.enabled=true. Permite mayor throughput sin escalar el pool de hilos del servidor.

**MapStruct**
Generación de código en tiempo de compilación para mapeos DTO a Entidad, sin reflexión en runtime. Los campos id, trackingCode, createdAt y status nunca son sobreescritos por el cliente.

**H2 en memoria**
Base de datos embebida que elimina dependencias externas para levantar el proyecto. La consola /h2-console permite inspeccionar datos en tiempo real durante el desarrollo.

**JpaSpecificationExecutor + Specification dinámica**
Permite combinar filtros opcionales status, type, destinationCity sin múltiples métodos de repositorio ni queries condicionales. Si todos los parámetros son nulos, devuelve todos los registros.

**Máquina de estados con Map**
VALID_TRANSITIONS define explícitamente las transiciones permitidas. La lógica de cancelación DELETE usa un switch separado con mensajes de negocio específicos por estado, devolviendo 422 Unprocessable Entity con detalle claro.

**trackingCode en dos saves**
El primer save obtiene el id generado por la base de datos. Luego se genera el código ENV-YYYYMMDD-XXXXX y se persiste en un segundo save, garantizando unicidad basada en el PK real.

**GlobalExceptionHandler centralizado**
Un único @RestControllerAdvice maneja todas las excepciones y devuelve un ErrorResponse uniforme con timestamp, status, error, message y path. Los errores de validación incluyen el detalle por campo.

## Qué mejoraría con más tiempo

- PostgreSQL + Flyway: reemplazar H2 por una base de datos persistente con migraciones versionadas.
- Spring Security + JWT: autenticación y autorización por roles operador y administrador.
- Paginación: Pageable en el listado general para manejar grandes volúmenes de datos.
- Eventos de dominio: publicar eventos ShipmentCreated y ShipmentDelivered vía Spring Events o mensajería con Kafka o RabbitMQ para desacoplar integraciones externas.
- Tests de integración: @SpringBootTest con Testcontainers PostgreSQL para validar el stack completo, complementado con tests unitarios del servicio con Mockito.
- Caché: @Cacheable en consultas de solo lectura frecuentes con Redis.
- Actuator + Micrometer: métricas y health checks listos para despliegue en Kubernetes.

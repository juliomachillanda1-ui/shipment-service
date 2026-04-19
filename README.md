# Shipment Service

Microservicio Spring Boot para la gestión de envíos (ABM completo).

## Cómo levantar

```bash
# Con Maven Wrapper (si está generado)
./mvnw spring-boot:run

# Con Maven instalado
mvn spring-boot:run
```

> Para generar el wrapper: `mvn wrapper:wrapper`

## URLs útiles

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

**H2 Console:** JDBC URL `jdbc:h2:mem:shipmentdb`, usuario `sa`, sin contraseña.

## Endpoints

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/shipments` | Crear envío |
| GET | `/api/shipments` | Listar (filtros: `status`, `type`, `destinationCity`) |
| GET | `/api/shipments/{id}` | Buscar por ID |
| GET | `/api/shipments/tracking/{trackingCode}` | Buscar por tracking |
| PUT | `/api/shipments/{id}` | Actualizar |
| DELETE | `/api/shipments/{id}` | Baja lógica (→ CANCELADO) |
| PATCH | `/api/shipments/{id}/status` | Cambiar estado |
| GET | `/api/shipments/report` | Reporte por estado |

## Decisiones de diseño

**Virtual Threads:** habilitados con `spring.threads.virtual.enabled=true`. Permite mayor throughput sin escalar el pool de hilos, aprovechando Java 21 Project Loom.

**MapStruct:** generación de código en tiempo de compilación para mapeos DTO ↔ Entidad, sin reflexión en runtime. Los campos `id`, `trackingCode`, `createdAt` y `status` nunca son sobreescritos por el cliente.

**H2 en memoria:** base de datos embebida que simplifica el setup local. La consola web `/h2-console` permite inspeccionar datos en tiempo real.

**JpaSpecificationExecutor + Specification dinámica:** permite combinar filtros opcionales (`status`, `type`, `destinationCity`) sin escribir múltiples métodos de repositorio ni queries condicionales con `@Query`.

**Máquina de estados con Map:** `VALID_TRANSITIONS` define explícitamente las transiciones permitidas. Cualquier transición fuera del mapa lanza `InvalidStateTransitionException` (409). La lógica de cancelación (DELETE) usa un switch separado con mensajes de negocio específicos por estado (`BusinessRuleException`, 422).

**trackingCode en dos saves:** el primer `save()` obtiene el `id` generado por la BD, luego se genera el código `ENV-YYYYMMDD-XXXXX` y se hace un segundo `save()`. Esto garantiza unicidad basada en el PK real.

## Qué mejoraría en producción

- **PostgreSQL + Flyway:** reemplazar H2 por una base de datos persistente con migraciones versionadas.
- **Spring Security + JWT:** autenticación y autorización por roles (operador, administrador).
- **Paginación:** `Pageable` en el listado general para manejar grandes volúmenes.
- **Eventos de dominio:** publicar eventos (`ShipmentCreated`, `ShipmentDelivered`) vía Spring Events o mensajería (Kafka/RabbitMQ) para desacoplar integraciones.
- **Tests de integración:** `@SpringBootTest` con Testcontainers (PostgreSQL) para validar el stack completo, más tests unitarios de servicio con Mockito.
- **Caché:** `@Cacheable` en consultas de solo lectura con Redis.
- **Actuator + Micrometer:** métricas y health checks listos para Kubernetes.

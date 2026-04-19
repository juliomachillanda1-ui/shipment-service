package com.shipment.controller;

import com.shipment.domain.ShipmentStatus;
import com.shipment.domain.ShipmentType;
import com.shipment.dto.ShipmentRequestDto;
import com.shipment.dto.ShipmentResponseDto;
import com.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
@Tag(name = "Shipments", description = "Gestión de envíos")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo envío")
    @ApiResponse(responseCode = "201", description = "Envío creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<ShipmentResponseDto> create(@Valid @RequestBody ShipmentRequestDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar envíos con filtros opcionales por estado, tipo y ciudad")
    @ApiResponse(responseCode = "200", description = "Lista de envíos")
    public ResponseEntity<List<ShipmentResponseDto>> findAll(
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) ShipmentType type,
            @RequestParam(required = false) String destinationCity) {
        return ResponseEntity.ok(service.findAll(status, type, destinationCity));
    }

    @GetMapping("/report")
    @Operation(summary = "Reporte de cantidad de envíos por estado")
    @ApiResponse(responseCode = "200", description = "Mapa de estado → cantidad")
    public ResponseEntity<Map<String, Long>> getReport() {
        return ResponseEntity.ok(service.getReport());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener envío por ID")
    @ApiResponse(responseCode = "200", description = "Envío encontrado")
    @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    public ResponseEntity<ShipmentResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/tracking/{trackingCode}")
    @Operation(summary = "Obtener envío por código de seguimiento")
    @ApiResponse(responseCode = "200", description = "Envío encontrado")
    @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    public ResponseEntity<ShipmentResponseDto> findByTrackingCode(@PathVariable String trackingCode) {
        return ResponseEntity.ok(service.findByTrackingCode(trackingCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un envío")
    @ApiResponse(responseCode = "200", description = "Envío actualizado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    @ApiResponse(responseCode = "422", description = "No se puede modificar en el estado actual")
    public ResponseEntity<ShipmentResponseDto> update(@PathVariable Long id, @Valid @RequestBody ShipmentRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar un envío (baja lógica, solo si está PENDIENTE)")
    @ApiResponse(responseCode = "200", description = "Envío cancelado")
    @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    @ApiResponse(responseCode = "422", description = "No se puede cancelar en el estado actual")
    public ResponseEntity<ShipmentResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar el estado de un envío según transiciones válidas")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    @ApiResponse(responseCode = "409", description = "Transición de estado inválida")
    public ResponseEntity<ShipmentResponseDto> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ShipmentStatus newStatus = ShipmentStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(service.changeStatus(id, newStatus));
    }
}

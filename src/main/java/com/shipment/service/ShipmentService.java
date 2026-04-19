package com.shipment.service;

import com.shipment.domain.Shipment;
import com.shipment.domain.ShipmentStatus;
import com.shipment.domain.ShipmentType;
import com.shipment.dto.ShipmentRequestDto;
import com.shipment.dto.ShipmentResponseDto;
import com.shipment.exception.BusinessRuleException;
import com.shipment.exception.InvalidStateTransitionException;
import com.shipment.exception.ShipmentNotFoundException;
import com.shipment.mapper.ShipmentMapper;
import com.shipment.repository.ShipmentRepository;
import com.shipment.util.TrackingCodeGenerator;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class ShipmentService {

    private static final Map<ShipmentStatus, Set<ShipmentStatus>> VALID_TRANSITIONS = Map.of(
            ShipmentStatus.PENDIENTE, Set.of(ShipmentStatus.EN_TRANSITO, ShipmentStatus.CANCELADO),
            ShipmentStatus.EN_TRANSITO, Set.of(ShipmentStatus.ENTREGADO)
    );

    private final ShipmentRepository repository;
    private final ShipmentMapper mapper;
    private final TrackingCodeGenerator trackingCodeGenerator;

    public ShipmentService(ShipmentRepository repository, ShipmentMapper mapper, TrackingCodeGenerator trackingCodeGenerator) {
        this.repository = repository;
        this.mapper = mapper;
        this.trackingCodeGenerator = trackingCodeGenerator;
    }

    public ShipmentResponseDto create(ShipmentRequestDto dto) {
        Shipment shipment = mapper.toEntity(dto);
        Shipment saved = repository.save(shipment);
        saved.setTrackingCode(trackingCodeGenerator.generate(saved.getId()));
        return mapper.toResponseDto(repository.save(saved));
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> findAll(ShipmentStatus status, ShipmentType type, String destinationCity) {
        Specification<Shipment> spec = buildSpecification(status, type, destinationCity);
        return repository.findAll(spec).stream().map(mapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public ShipmentResponseDto findById(Long id) {
        return mapper.toResponseDto(getById(id));
    }

    @Transactional(readOnly = true)
    public ShipmentResponseDto findByTrackingCode(String trackingCode) {
        return repository.findByTrackingCode(trackingCode)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new ShipmentNotFoundException(trackingCode));
    }

    public ShipmentResponseDto update(Long id, ShipmentRequestDto dto) {
        Shipment shipment = getById(id);
        if (shipment.getStatus() == ShipmentStatus.ENTREGADO || shipment.getStatus() == ShipmentStatus.CANCELADO) {
            throw new BusinessRuleException("No se puede modificar un envío con estado: " + shipment.getStatus());
        }
        mapper.updateEntityFromDto(dto, shipment);
        return mapper.toResponseDto(repository.save(shipment));
    }

    public ShipmentResponseDto cancel(Long id) {
        Shipment shipment = getById(id);
        switch (shipment.getStatus()) {
            case PENDIENTE -> shipment.setStatus(ShipmentStatus.CANCELADO);
            case EN_TRANSITO -> throw new BusinessRuleException("No se puede cancelar un envío en tránsito");
            case ENTREGADO -> throw new BusinessRuleException("No se puede cancelar un envío ya entregado");
            case CANCELADO -> throw new BusinessRuleException("El envío ya está cancelado");
        }
        return mapper.toResponseDto(repository.save(shipment));
    }

    public ShipmentResponseDto changeStatus(Long id, ShipmentStatus newStatus) {
        Shipment shipment = getById(id);
        Set<ShipmentStatus> allowed = VALID_TRANSITIONS.getOrDefault(shipment.getStatus(), Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidStateTransitionException(shipment.getStatus(), newStatus);
        }
        shipment.setStatus(newStatus);
        return mapper.toResponseDto(repository.save(shipment));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getReport() {
        Map<String, Long> report = new LinkedHashMap<>();
        for (ShipmentStatus s : ShipmentStatus.values()) {
            report.put(s.name(), 0L);
        }
        repository.countByStatus()
                .forEach(row -> report.put(((ShipmentStatus) row[0]).name(), (Long) row[1]));
        return report;
    }

    private Shipment getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ShipmentNotFoundException(id));
    }

    private Specification<Shipment> buildSpecification(ShipmentStatus status, ShipmentType type, String destinationCity) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (destinationCity != null && !destinationCity.isBlank()) {
                predicates.add(cb.equal(root.get("destinationCity"), destinationCity));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

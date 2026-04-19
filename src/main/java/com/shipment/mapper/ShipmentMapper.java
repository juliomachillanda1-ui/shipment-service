package com.shipment.mapper;

import com.shipment.domain.Shipment;
import com.shipment.dto.ShipmentRequestDto;
import com.shipment.dto.ShipmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Shipment toEntity(ShipmentRequestDto dto);

    ShipmentResponseDto toResponseDto(Shipment shipment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trackingCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShipmentRequestDto dto, @MappingTarget Shipment entity);
}

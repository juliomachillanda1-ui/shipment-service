package com.shipment.mapper;

import com.shipment.domain.Shipment;
import com.shipment.dto.ShipmentRequestDto;
import com.shipment.dto.ShipmentResponseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T18:03:51-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class ShipmentMapperImpl implements ShipmentMapper {

    @Override
    public Shipment toEntity(ShipmentRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Shipment shipment = new Shipment();

        shipment.setRecipientName( dto.getRecipientName() );
        shipment.setDeliveryAddress( dto.getDeliveryAddress() );
        shipment.setDestinationCity( dto.getDestinationCity() );
        shipment.setDestinationProvince( dto.getDestinationProvince() );
        shipment.setPostalCode( dto.getPostalCode() );
        shipment.setType( dto.getType() );

        return shipment;
    }

    @Override
    public ShipmentResponseDto toResponseDto(Shipment shipment) {
        if ( shipment == null ) {
            return null;
        }

        ShipmentResponseDto shipmentResponseDto = new ShipmentResponseDto();

        shipmentResponseDto.setId( shipment.getId() );
        shipmentResponseDto.setTrackingCode( shipment.getTrackingCode() );
        shipmentResponseDto.setRecipientName( shipment.getRecipientName() );
        shipmentResponseDto.setDeliveryAddress( shipment.getDeliveryAddress() );
        shipmentResponseDto.setDestinationCity( shipment.getDestinationCity() );
        shipmentResponseDto.setDestinationProvince( shipment.getDestinationProvince() );
        shipmentResponseDto.setPostalCode( shipment.getPostalCode() );
        shipmentResponseDto.setType( shipment.getType() );
        shipmentResponseDto.setStatus( shipment.getStatus() );
        shipmentResponseDto.setCreatedAt( shipment.getCreatedAt() );
        shipmentResponseDto.setUpdatedAt( shipment.getUpdatedAt() );

        return shipmentResponseDto;
    }

    @Override
    public void updateEntityFromDto(ShipmentRequestDto dto, Shipment entity) {
        if ( dto == null ) {
            return;
        }

        entity.setRecipientName( dto.getRecipientName() );
        entity.setDeliveryAddress( dto.getDeliveryAddress() );
        entity.setDestinationCity( dto.getDestinationCity() );
        entity.setDestinationProvince( dto.getDestinationProvince() );
        entity.setPostalCode( dto.getPostalCode() );
        entity.setType( dto.getType() );
    }
}

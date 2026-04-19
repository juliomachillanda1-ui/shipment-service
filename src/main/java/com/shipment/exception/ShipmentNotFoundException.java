package com.shipment.exception;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(Long id) {
        super("Envío no encontrado con id: " + id);
    }

    public ShipmentNotFoundException(String trackingCode) {
        super("Envío no encontrado con trackingCode: " + trackingCode);
    }
}

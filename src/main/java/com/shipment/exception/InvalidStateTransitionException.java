package com.shipment.exception;

import com.shipment.domain.ShipmentStatus;

public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(ShipmentStatus from, ShipmentStatus to) {
        super("Transición de estado inválida: " + from + " → " + to);
    }
}

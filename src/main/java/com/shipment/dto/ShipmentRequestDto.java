package com.shipment.dto;

import com.shipment.domain.ShipmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ShipmentRequestDto {

    @Schema(description = "Nombre del destinatario", example = "Juan Pérez", maxLength = 100)
    @NotBlank
    @Size(max = 100)
    private String recipientName;

    @Schema(description = "Dirección de entrega completa", example = "Av. Corrientes 1234, Piso 3", maxLength = 255)
    @NotBlank
    @Size(max = 255)
    private String deliveryAddress;

    @Schema(description = "Ciudad de destino", example = "Buenos Aires")
    @NotBlank
    private String destinationCity;

    @Schema(description = "Provincia de destino", example = "Buenos Aires")
    @NotBlank
    private String destinationProvince;

    @Schema(description = "Código postal de 4 dígitos", example = "1043")
    @NotBlank
    @Pattern(regexp = "\\d{4}", message = "El código postal debe tener exactamente 4 dígitos numéricos")
    private String postalCode;

    @Schema(description = "Tipo de envío", example = "ESTANDAR", allowableValues = {"ESTANDAR", "EXPRESO", "FRAGIL"})
    @NotNull
    private ShipmentType type;

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDestinationProvince() { return destinationProvince; }
    public void setDestinationProvince(String destinationProvince) { this.destinationProvince = destinationProvince; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public ShipmentType getType() { return type; }
    public void setType(ShipmentType type) { this.type = type; }
}

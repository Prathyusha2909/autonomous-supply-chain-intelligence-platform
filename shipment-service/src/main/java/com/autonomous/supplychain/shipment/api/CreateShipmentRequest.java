package com.autonomous.supplychain.shipment.api;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateShipmentRequest(
        @NotBlank String orderNumber,
        @NotBlank String carrier,
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @Future Instant plannedEta
) {
}

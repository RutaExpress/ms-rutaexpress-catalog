package com.rutaexpress.catalog.adapter.in.web.dto;

import com.rutaexpress.catalog.application.dto.UpdateServiceCommand;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO de entrada para actualizar un servicio.
 */
public record UpdateServiceRequest(
        @PositiveOrZero(message = "La tarifa no puede ser negativa")
        BigDecimal basePrice,

        Integer capacityDelta
) {
    public UpdateServiceCommand toCommand() {
        return new UpdateServiceCommand(basePrice, capacityDelta);
    }
}
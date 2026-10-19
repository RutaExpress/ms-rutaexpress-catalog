package com.rutaexpress.catalog.application.dto;

import java.math.BigDecimal;

/**
 * Comando para actualizar tarifa y/o capacidad de un servicio existente.
 */
public record UpdateServiceCommand(
        BigDecimal basePrice,
        Integer capacityDelta
) {
    public UpdateServiceCommand {
        if (basePrice != null && basePrice.signum() < 0) {
            throw new IllegalArgumentException("La tarifa no puede ser negativa");
        }
    }
}
package com.rutaexpress.catalog.application.dto;

import java.math.BigDecimal;

/**
 * DTO de salida para devolver información de un servicio.
 */
public record ServiceResponse(
        String id,
        String name,
        BigDecimal basePrice,
        String currency,
        int availableCapacity,
        boolean active
) {
}
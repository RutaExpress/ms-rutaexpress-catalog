package com.rutaexpress.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;

// datos para editar un servicio: nueva tarifa y/o cuánto sube (+) o baja (-) la capacidad; los dos opcionales
public record UpdateServiceRequest(
        @PositiveOrZero(message = "La tarifa no puede ser negativa")
        BigDecimal basePrice,

        Integer capacityDelta
) {}

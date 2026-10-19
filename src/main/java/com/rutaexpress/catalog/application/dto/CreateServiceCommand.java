package com.rutaexpress.catalog.application.dto;

import java.math.BigDecimal;

/**
 * Comando para crear un nuevo servicio de envío.
 * Inmutable.
 */
public record CreateServiceCommand(
        String name,
        BigDecimal basePrice,
        String currency,
        int initialCapacity
) {
    public CreateServiceCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (basePrice == null || basePrice.signum() < 0) {
            throw new IllegalArgumentException("La tarifa debe ser positiva");
        }
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        if (currency == null || currency.isBlank()) {
            currency = "CLP";
        }
    }
}
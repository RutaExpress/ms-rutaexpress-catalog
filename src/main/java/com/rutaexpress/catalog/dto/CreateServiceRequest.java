package com.rutaexpress.catalog.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// datos para crear un servicio (junta CreateServiceRequest web y CreateServiceCommand de Yanfranco)
public record CreateServiceRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "La tarifa base es obligatoria")
        @PositiveOrZero(message = "La tarifa no puede ser negativa")
        BigDecimal basePrice,

        String currency,                 // opcional: CLP si no viene

        @Min(value = 0, message = "La capacidad no puede ser negativa")
        int initialCapacity
) {
    // currencyOrClp (moneda o CLP): la moneda pedida, o CLP si viene vacía
    public String currencyOrClp() {
        return currency == null || currency.isBlank() ? "CLP" : currency;
    }
}

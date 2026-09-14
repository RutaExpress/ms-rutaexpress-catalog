package com.rutaexpress.catalog.adapter.in.web.dto;

import com.rutaexpress.catalog.application.dto.CreateServiceCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO de entrada para crear un servicio.
 * Aquí sí podemos tener anotaciones de validación de Jakarta.
 */
public record CreateServiceRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "La tarifa base es obligatoria")
        @PositiveOrZero(message = "La tarifa no puede ser negativa")
        BigDecimal basePrice,

        String currency,

        @Min(value = 0, message = "La capacidad no puede ser negativa")
        int initialCapacity
) {
    public CreateServiceCommand toCommand() {
        return new CreateServiceCommand(
                name,
                basePrice,
                currency == null || currency.isBlank() ? "CLP" : currency,
                initialCapacity
        );
    }
}
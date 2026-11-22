package com.rutaexpress.catalog.dto;

import java.math.BigDecimal;

import com.rutaexpress.catalog.model.ShippingService;

// lo que devuelve la API: la entidad nunca sale directo (convención de capas)
public record ServiceResponse(
        String id,
        String name,
        BigDecimal basePrice,
        String currency,
        int availableCapacity,
        boolean active
) {
    // from (desde): convierte la entidad en la respuesta (antes toResponse en el service)
    public static ServiceResponse from(ShippingService s) {
        return new ServiceResponse(s.getId(), s.getName(), s.getBasePrice().amount(), s.getBasePrice().currency(),
                s.getAvailableCapacity(), s.isActive());
    }
}

package com.rutaexpress.catalog.adapter.out.persistence;

import com.rutaexpress.catalog.domain.model.Money;
import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;

/**
 * Traduce entre el modelo de dominio (ShippingService) y
 * la entidad JPA (ServiceJpaEntity).
 *
 * Es la frontera entre "el dominio" y "la base de datos".
 */
public final class ServiceMapper {

    private ServiceMapper() {
    }

    /** Dominio → JPA */
    public static ServiceJpaEntity toJpaEntity(ShippingService domain) {
        return new ServiceJpaEntity(
                domain.id().value(),
                domain.name(),
                domain.basePrice().amount(),
                domain.basePrice().currency(),
                domain.availableCapacity(),
                domain.isActive()
        );
    }

    /** JPA → Dominio */
    public static ShippingService toDomain(ServiceJpaEntity entity) {
        return ShippingService.rehydrate(
                ServiceId.of(entity.getId()),
                entity.getName(),
                Money.of(entity.getBasePrice(), entity.getCurrency()),
                entity.getAvailableCapacity(),
                entity.isActive()
        );
    }
}
package com.rutaexpress.catalog.domain.port.out;

import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio.
 * Define las operaciones de persistencia que el dominio NECESITA.
 * No sabe si detrás hay JPA, MongoDB, Oracle o un mock.
 */
public interface ServiceRepositoryPort {

    ShippingService save(ShippingService service);

    Optional<ShippingService> findById(ServiceId id);

    List<ShippingService> findAll();

    List<ShippingService> findAllActive();

    void deleteById(ServiceId id);

    boolean existsByName(String name);
}
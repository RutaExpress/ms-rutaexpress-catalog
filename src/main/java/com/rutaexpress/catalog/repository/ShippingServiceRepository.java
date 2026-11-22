package com.rutaexpress.catalog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rutaexpress.catalog.model.ShippingService;

// acceso a la tabla shipping_services (reemplaza a ServiceRepositoryPort, ServicePersistenceAdapter y ServiceJpaRepository)
@Repository
public interface ShippingServiceRepository extends JpaRepository<ShippingService, String> {

    // findByActiveTrue (servicios activos): Spring Data arma la consulta desde el nombre
    List<ShippingService> findByActiveTrue();

    // existsByName (¿existe ese nombre?): para no repetir servicios
    boolean existsByName(String name);
}

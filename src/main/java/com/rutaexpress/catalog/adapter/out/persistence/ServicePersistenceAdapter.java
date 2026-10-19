package com.rutaexpress.catalog.adapter.out.persistence;

import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;
import com.rutaexpress.catalog.domain.port.out.ServiceRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto ServiceRepositoryPort
 * usando JPA como tecnología de persistencia.
 */
@Component
public class ServicePersistenceAdapter implements ServiceRepositoryPort {

    private final ServiceJpaRepository jpaRepository;

    public ServicePersistenceAdapter(ServiceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ShippingService save(ShippingService service) {
        ServiceJpaEntity entity = ServiceMapper.toJpaEntity(service);
        ServiceJpaEntity saved = jpaRepository.save(entity);
        return ServiceMapper.toDomain(saved);
    }

    @Override
    public Optional<ShippingService> findById(ServiceId id) {
        return jpaRepository.findById(id.value())
                .map(ServiceMapper::toDomain);
    }

    @Override
    public List<ShippingService> findAll() {
        return jpaRepository.findAll().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }

    @Override
    public List<ShippingService> findAllActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(ServiceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(ServiceId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
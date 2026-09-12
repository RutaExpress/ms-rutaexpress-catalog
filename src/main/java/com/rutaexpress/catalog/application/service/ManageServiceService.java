package com.rutaexpress.catalog.application.service;

import com.rutaexpress.catalog.application.dto.CreateServiceCommand;
import com.rutaexpress.catalog.application.dto.ServiceResponse;
import com.rutaexpress.catalog.application.dto.UpdateServiceCommand;
import com.rutaexpress.catalog.domain.exception.ServiceNotFoundException;
import com.rutaexpress.catalog.domain.model.Money;
import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;
import com.rutaexpress.catalog.domain.port.in.ManageServiceUseCase;
import com.rutaexpress.catalog.domain.port.out.ServiceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del caso de uso.
 * Orquesta el dominio y los puertos, sin contener lógica de negocio propia.
 */
@Service
@Transactional
public class ManageServiceService implements ManageServiceUseCase {

    private final ServiceRepositoryPort repository;

    public ManageServiceService(ServiceRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public ServiceResponse create(CreateServiceCommand command) {
        if (repository.existsByName(command.name())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre: " + command.name());
        }
        Money price = Money.of(command.basePrice(), command.currency());
        ShippingService service = ShippingService.create(command.name(), price, command.initialCapacity());
        ShippingService saved = repository.save(service);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse findById(String id) {
        ShippingService service = repository.findById(ServiceId.of(id))
                .orElseThrow(() -> new ServiceNotFoundException(id));
        return toResponse(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> findAllActive() {
        return repository.findAllActive().stream().map(this::toResponse).toList();
    }

    @Override
    public ServiceResponse update(String id, UpdateServiceCommand command) {
        ShippingService service = repository.findById(ServiceId.of(id))
                .orElseThrow(() -> new ServiceNotFoundException(id));

        if (command.basePrice() != null) {
            service.updatePrice(Money.of(command.basePrice(), service.basePrice().currency()));
        }
        if (command.capacityDelta() != null && command.capacityDelta() != 0) {
            if (command.capacityDelta() > 0) {
                service.increaseCapacity(command.capacityDelta());
            } else {
                for (int i = 0; i < Math.abs(command.capacityDelta()); i++) {
                    service.decreaseCapacity();
                }
            }
        }
        return toResponse(repository.save(service));
    }

    @Override
    public ServiceResponse deactivate(String id) {
        ShippingService service = repository.findById(ServiceId.of(id))
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.deactivate();
        return toResponse(repository.save(service));
    }

    @Override
    public ServiceResponse activate(String id) {
        ShippingService service = repository.findById(ServiceId.of(id))
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.activate();
        return toResponse(repository.save(service));
    }

    @Override
    public void delete(String id) {
        ServiceId serviceId = ServiceId.of(id);
        if (repository.findById(serviceId).isEmpty()) {
            throw new ServiceNotFoundException(id);
        }
        repository.deleteById(serviceId);
    }

    private ServiceResponse toResponse(ShippingService s) {
        return new ServiceResponse(
                s.id().value(),
                s.name(),
                s.basePrice().amount(),
                s.basePrice().currency(),
                s.availableCapacity(),
                s.isActive()
        );
    }
}
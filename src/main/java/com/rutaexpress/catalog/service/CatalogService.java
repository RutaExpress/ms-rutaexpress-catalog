package com.rutaexpress.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rutaexpress.catalog.dto.CreateServiceRequest;
import com.rutaexpress.catalog.dto.ServiceResponse;
import com.rutaexpress.catalog.dto.UpdateServiceRequest;
import com.rutaexpress.catalog.exception.DuplicateServiceNameException;
import com.rutaexpress.catalog.exception.ServiceNotFoundException;
import com.rutaexpress.catalog.model.Money;
import com.rutaexpress.catalog.model.ShippingService;
import com.rutaexpress.catalog.repository.ShippingServiceRepository;

import lombok.RequiredArgsConstructor;

// reglas del catálogo (antes ManageServiceService + ManageServiceUseCase): clase concreta, sin interfaz
// las reglas de cada servicio viven en la entidad; aquí se busca, se guarda y se convierte a DTO
@Service
@Transactional
@RequiredArgsConstructor
public class CatalogService {

    private final ShippingServiceRepository repository;

    // create (crear): nombre único; moneda CLP si no viene
    public ServiceResponse create(CreateServiceRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DuplicateServiceNameException(request.name());
        }
        Money precio = Money.of(request.basePrice(), request.currencyOrClp());
        ShippingService nuevo = ShippingService.create(request.name(), precio, request.initialCapacity());
        return ServiceResponse.from(repository.save(nuevo));
    }

    @Transactional(readOnly = true)
    public ServiceResponse findById(String id) {
        return ServiceResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> findAll() {
        return repository.findAll().stream().map(ServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> findAllActive() {
        return repository.findByActiveTrue().stream().map(ServiceResponse::from).toList();
    }

    // update (editar): tarifa en la misma moneda y capacidad que sube (+) o baja (-) de a una
    public ServiceResponse update(String id, UpdateServiceRequest request) {
        ShippingService servicio = buscar(id);
        if (request.basePrice() != null) {
            servicio.updatePrice(Money.of(request.basePrice(), servicio.getBasePrice().currency()));
        }
        Integer delta = request.capacityDelta();
        if (delta != null && delta > 0) {
            servicio.increaseCapacity(delta);
        } else if (delta != null && delta < 0) {
            for (int i = 0; i < -delta; i++) {
                servicio.decreaseCapacity();          // si se acaba el cupo lanza 409 y @Transactional deshace todo
            }
        }
        return ServiceResponse.from(repository.save(servicio));
    }

    public ServiceResponse deactivate(String id) {
        ShippingService servicio = buscar(id);
        servicio.deactivate();
        return ServiceResponse.from(repository.save(servicio));
    }

    public ServiceResponse activate(String id) {
        ShippingService servicio = buscar(id);
        servicio.activate();
        return ServiceResponse.from(repository.save(servicio));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ServiceNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // buscar: el servicio o 404
    private ShippingService buscar(String id) {
        return repository.findById(id).orElseThrow(() -> new ServiceNotFoundException(id));
    }
}

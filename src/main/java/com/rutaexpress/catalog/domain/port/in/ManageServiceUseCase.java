package com.rutaexpress.catalog.domain.port.in;

import com.rutaexpress.catalog.application.dto.CreateServiceCommand;
import com.rutaexpress.catalog.application.dto.ServiceResponse;
import com.rutaexpress.catalog.application.dto.UpdateServiceCommand;

import java.util.List;

/**
 * Puerto de entrada del dominio.
 * Expone QUÉ casos de uso puede realizar el exterior sobre el agregado ShippingService.
 */
public interface ManageServiceUseCase {

    ServiceResponse create(CreateServiceCommand command);

    ServiceResponse findById(String id);

    List<ServiceResponse> findAll();

    List<ServiceResponse> findAllActive();

    ServiceResponse update(String id, UpdateServiceCommand command);

    ServiceResponse deactivate(String id);

    ServiceResponse activate(String id);

    void delete(String id);
}
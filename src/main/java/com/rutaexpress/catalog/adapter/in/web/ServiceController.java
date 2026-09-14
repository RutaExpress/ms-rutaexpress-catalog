package com.rutaexpress.catalog.adapter.in.web;

import com.rutaexpress.catalog.adapter.in.web.dto.CreateServiceRequest;
import com.rutaexpress.catalog.adapter.in.web.dto.UpdateServiceRequest;
import com.rutaexpress.catalog.application.dto.ServiceResponse;
import com.rutaexpress.catalog.domain.port.in.ManageServiceUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST.
 * Traduce HTTP ↔ casos de uso.
 */
@RestController
@RequestMapping("/api/catalog/services")
public class ServiceController {

    private final ManageServiceUseCase useCase;

    public ServiceController(ManageServiceUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> create(@Valid @RequestBody CreateServiceRequest request) {
        ServiceResponse created = useCase.create(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<ServiceResponse> findAll() {
        return useCase.findAll();
    }

    @GetMapping("/active")
    public List<ServiceResponse> findActive() {
        return useCase.findAllActive();
    }

    @GetMapping("/{id}")
    public ServiceResponse findById(@PathVariable String id) {
        return useCase.findById(id);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable String id,
                                  @Valid @RequestBody UpdateServiceRequest request) {
        return useCase.update(id, request.toCommand());
    }

    @PatchMapping("/{id}/deactivate")
    public ServiceResponse deactivate(@PathVariable String id) {
        return useCase.deactivate(id);
    }

    @PatchMapping("/{id}/activate")
    public ServiceResponse activate(@PathVariable String id) {
        return useCase.activate(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        useCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
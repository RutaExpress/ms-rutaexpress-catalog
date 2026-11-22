package com.rutaexpress.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rutaexpress.catalog.dto.CreateServiceRequest;
import com.rutaexpress.catalog.dto.ServiceResponse;
import com.rutaexpress.catalog.dto.UpdateServiceRequest;
import com.rutaexpress.catalog.service.CatalogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// endpoints de catalog en /api/v1 (contrato 3; antes /api/catalog/services); los llama el BFF
@RestController
@RequestMapping("/api/v1/catalog/services")
@RequiredArgsConstructor
public class ServiceController {

    private final CatalogService catalogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse create(@Valid @RequestBody CreateServiceRequest request) {
        return catalogService.create(request);
    }

    @GetMapping
    public List<ServiceResponse> findAll() {
        return catalogService.findAll();
    }

    @GetMapping("/active")
    public List<ServiceResponse> findActive() {
        return catalogService.findAllActive();
    }

    @GetMapping("/{id}")
    public ServiceResponse findById(@PathVariable String id) {
        return catalogService.findById(id);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable String id, @Valid @RequestBody UpdateServiceRequest request) {
        return catalogService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public ServiceResponse deactivate(@PathVariable String id) {
        return catalogService.deactivate(id);
    }

    @PatchMapping("/{id}/activate")
    public ServiceResponse activate(@PathVariable String id) {
        return catalogService.activate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        catalogService.delete(id);
    }
}

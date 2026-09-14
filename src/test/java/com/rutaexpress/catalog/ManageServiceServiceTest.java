package com.rutaexpress.catalog;

import com.rutaexpress.catalog.application.dto.CreateServiceCommand;
import com.rutaexpress.catalog.application.dto.ServiceResponse;
import com.rutaexpress.catalog.application.dto.UpdateServiceCommand;
import com.rutaexpress.catalog.application.service.ManageServiceService;
import com.rutaexpress.catalog.domain.exception.ServiceNotFoundException;
import com.rutaexpress.catalog.domain.model.Money;
import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;
import com.rutaexpress.catalog.domain.port.out.ServiceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests del caso de uso ManageServiceService.
 * Usa Mockito para simular el puerto de salida.
 */
@ExtendWith(MockitoExtension.class)
class ManageServiceServiceTest {

    @Mock
    private ServiceRepositoryPort repository;

    @InjectMocks
    private ManageServiceService service;

    private ShippingService sampleService;

    @BeforeEach
    void setUp() {
        sampleService = ShippingService.create("Express", Money.of(new BigDecimal("4990"), "CLP"), 10);
    }

    @Test
    void deberiaCrearServicio() {
        CreateServiceCommand command = new CreateServiceCommand("Express", new BigDecimal("4990"), "CLP", 10);
        when(repository.existsByName("Express")).thenReturn(false);
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        ServiceResponse response = service.create(command);

        assertEquals("Express", response.name());
        assertEquals(10, response.availableCapacity());
        verify(repository, times(1)).save(any(ShippingService.class));
    }

    @Test
    void deberiaFallarAlCrearSiElNombreYaExiste() {
        CreateServiceCommand command = new CreateServiceCommand("Express", new BigDecimal("4990"), "CLP", 10);
        when(repository.existsByName("Express")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(command));
        verify(repository, never()).save(any());
    }

    @Test
    void deberiaBuscarPorId() {
        when(repository.findById(sampleService.id())).thenReturn(Optional.of(sampleService));

        ServiceResponse response = service.findById(sampleService.id().value());

        assertEquals(sampleService.id().value(), response.id());
    }

    @Test
    void deberiaFallarSiNoEncuentraPorId() {
        ServiceId id = ServiceId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> service.findById(id.value()));
    }

    @Test
    void deberiaListarTodos() {
        when(repository.findAll()).thenReturn(List.of(sampleService));

        List<ServiceResponse> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("Express", result.get(0).name());
    }

    @Test
    void deberiaActualizarPrecio() {
        UpdateServiceCommand command = new UpdateServiceCommand(new BigDecimal("5990"), null);
        when(repository.findById(sampleService.id())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        service.update(sampleService.id().value(), command);

        assertEquals(new BigDecimal("5990"), sampleService.basePrice().amount());
    }

    @Test
    void deberiaAumentarCapacidad() {
        UpdateServiceCommand command = new UpdateServiceCommand(null, 5);
        when(repository.findById(sampleService.id())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        service.update(sampleService.id().value(), command);

        assertEquals(15, sampleService.availableCapacity());
    }

    @Test
    void deberiaDesactivarServicio() {
        when(repository.findById(sampleService.id())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        ServiceResponse response = service.deactivate(sampleService.id().value());

        assertEquals(false, response.active());
    }

    @Test
    void deberiaEliminarServicioExistente() {
        when(repository.findById(sampleService.id())).thenReturn(Optional.of(sampleService));

        service.delete(sampleService.id().value());

        verify(repository, times(1)).deleteById(sampleService.id());
    }

    @Test
    void deberiaFallarAlEliminarServicioInexistente() {
        ServiceId id = ServiceId.generate();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> service.delete(id.value()));
        verify(repository, never()).deleteById(any());
    }
}
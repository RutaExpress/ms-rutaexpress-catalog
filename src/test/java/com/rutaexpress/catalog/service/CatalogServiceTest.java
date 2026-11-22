package com.rutaexpress.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rutaexpress.catalog.dto.CreateServiceRequest;
import com.rutaexpress.catalog.dto.ServiceResponse;
import com.rutaexpress.catalog.dto.UpdateServiceRequest;
import com.rutaexpress.catalog.exception.DuplicateServiceNameException;
import com.rutaexpress.catalog.exception.ServiceNotFoundException;
import com.rutaexpress.catalog.model.Money;
import com.rutaexpress.catalog.model.ShippingService;
import com.rutaexpress.catalog.repository.ShippingServiceRepository;

/**
 * Tests del service de catálogo (antes ManageServiceService) con Mockito.
 * Son los de Yanfranco adaptados: el mock es el repositorio de Spring Data (antes el puerto),
 * reciben los DTO de entrada (antes los Command) y el nombre repetido lanza DuplicateServiceNameException (409).
 */
@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    // dependencia simulada (repositorio de servicios de envío)
    @Mock
    private ShippingServiceRepository repository;

    // clase que se prueba, con el mock inyectado por el constructor
    @InjectMocks
    private CatalogService service;

    private ShippingService sampleService;

    @BeforeEach
    void setUp() {
        sampleService = ShippingService.create("Express", Money.of(new BigDecimal("4990"), "CLP"), 10);
    }

    @Test
    void deberiaCrearServicio() {
        CreateServiceRequest request = new CreateServiceRequest("Express", new BigDecimal("4990"), "CLP", 10);
        when(repository.existsByName("Express")).thenReturn(false);
        when(repository.save(any(ShippingService.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        ServiceResponse response = service.create(request);

        assertEquals("Express", response.name());
        assertEquals(10, response.availableCapacity());
        assertEquals("CLP", response.currency());
    }

    @Test
    void deberiaUsarClpSiNoVieneMoneda() {
        CreateServiceRequest request = new CreateServiceRequest("Express", new BigDecimal("4990"), null, 10);
        when(repository.existsByName("Express")).thenReturn(false);
        when(repository.save(any(ShippingService.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        service.create(request);

        ArgumentCaptor<ShippingService> captor = ArgumentCaptor.forClass(ShippingService.class);
        verify(repository).save(captor.capture());
        assertEquals("CLP", captor.getValue().getBasePrice().currency());
    }

    @Test
    void deberiaFallarAlCrearSiElNombreYaExiste() {
        CreateServiceRequest request = new CreateServiceRequest("Express", new BigDecimal("4990"), "CLP", 10);
        when(repository.existsByName("Express")).thenReturn(true);

        assertThrows(DuplicateServiceNameException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void deberiaBuscarPorId() {
        when(repository.findById(sampleService.getId())).thenReturn(Optional.of(sampleService));

        ServiceResponse response = service.findById(sampleService.getId());

        assertEquals(sampleService.getId(), response.id());
    }

    @Test
    void deberiaFallarSiNoEncuentraPorId() {
        when(repository.findById("no-existe")).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () -> service.findById("no-existe"));
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
        when(repository.findById(sampleService.getId())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        service.update(sampleService.getId(), new UpdateServiceRequest(new BigDecimal("5990"), null));

        assertEquals(new BigDecimal("5990"), sampleService.getBasePrice().amount());
    }

    @Test
    void deberiaAumentarCapacidad() {
        when(repository.findById(sampleService.getId())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        service.update(sampleService.getId(), new UpdateServiceRequest(null, 5));

        assertEquals(15, sampleService.getAvailableCapacity());
    }

    @Test
    void deberiaDesactivarServicio() {
        when(repository.findById(sampleService.getId())).thenReturn(Optional.of(sampleService));
        when(repository.save(any(ShippingService.class))).thenReturn(sampleService);

        ServiceResponse response = service.deactivate(sampleService.getId());

        assertFalse(response.active());
    }

    @Test
    void deberiaEliminarServicioExistente() {
        when(repository.existsById(sampleService.getId())).thenReturn(true);

        service.delete(sampleService.getId());

        verify(repository).deleteById(sampleService.getId());
    }

    @Test
    void deberiaFallarAlEliminarServicioInexistente() {
        when(repository.existsById("no-existe")).thenReturn(false);

        assertThrows(ServiceNotFoundException.class, () -> service.delete("no-existe"));
        verify(repository, never()).deleteById(any());
    }
}

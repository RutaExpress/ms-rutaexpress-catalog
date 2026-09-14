package com.rutaexpress.catalog;

import com.rutaexpress.catalog.domain.exception.CapacityExhaustedException;
import com.rutaexpress.catalog.domain.model.Money;
import com.rutaexpress.catalog.domain.model.ServiceId;
import com.rutaexpress.catalog.domain.model.ShippingService;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios del Aggregate Root ShippingService.
 * Sin Spring, solo JUnit 5 puro. Prueba las reglas de negocio del dominio.
 */
class ShippingServiceTest {

    private static final Money PRECIO_BASE = Money.of(new BigDecimal("4990"), "CLP");

    @Test
    void deberiaCrearServicioConDatosValidos() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 10);

        assertEquals("Express", service.name());
        assertEquals(PRECIO_BASE, service.basePrice());
        assertEquals(10, service.availableCapacity());
        assertTrue(service.isActive());
        assertNotEquals(null, service.id());
    }

    @Test
    void deberiaFallarSiNombreEsVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> ShippingService.create("", PRECIO_BASE, 10));
    }

    @Test
    void deberiaFallarSiPrecioEsNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> ShippingService.create("Express", null, 10));
    }

    @Test
    void deberiaFallarSiCapacidadInicialEsNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> ShippingService.create("Express", PRECIO_BASE, -1));
    }

    @Test
    void deberiaReducirCapacidadAlAceptarEnvio() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);

        service.decreaseCapacity();
        assertEquals(2, service.availableCapacity());

        service.decreaseCapacity();
        assertEquals(1, service.availableCapacity());
    }

    @Test
    void deberiaFallarAlReducirSiNoHayCapacidad() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 1);
        service.decreaseCapacity();

        assertThrows(CapacityExhaustedException.class, service::decreaseCapacity);
    }

    @Test
    void deberiaFallarAlOperarSobreServicioInactivo() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 5);
        service.deactivate();

        assertThrows(IllegalStateException.class, service::decreaseCapacity);
    }

    @Test
    void deberiaAumentarCapacidad() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);
        service.increaseCapacity(5);
        assertEquals(8, service.availableCapacity());
    }

    @Test
    void deberiaFallarAlAumentarConCantidadNegativa() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);
        assertThrows(IllegalArgumentException.class, () -> service.increaseCapacity(-1));
    }

    @Test
    void deberiaActualizarPrecio() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);
        Money nuevo = Money.of(new BigDecimal("5990"), "CLP");
        service.updatePrice(nuevo);
        assertEquals(nuevo, service.basePrice());
    }

    @Test
    void deberiaDesactivarYActivar() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);

        service.deactivate();
        assertFalse(service.isActive());

        service.activate();
        assertTrue(service.isActive());
    }

    @Test
    void deberiaSerIgualPorId() {
        ShippingService a = ShippingService.create("Express", PRECIO_BASE, 3);
        ShippingService b = ShippingService.rehydrate(
                a.id(), "Otro nombre", Money.of(BigDecimal.TEN, "USD"), 99, false
        );
        assertEquals(a, b); // mismo id = misma entidad
    }

    @Test
    void rehydrateDeberiaReconstruirSinGenerarNuevoId() {
        ServiceId id = ServiceId.of("test-id-123");
        ShippingService service = ShippingService.rehydrate(
                id, "Express", PRECIO_BASE, 5, true
        );
        assertEquals(id, service.id());
        assertEquals(5, service.availableCapacity());
    }
}
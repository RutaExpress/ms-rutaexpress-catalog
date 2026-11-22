package com.rutaexpress.catalog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.rutaexpress.catalog.exception.CapacityExhaustedException;

/**
 * Tests unitarios de ShippingService (servicio de envío), ahora entidad JPA con sus reglas.
 * Son los de Yanfranco adaptados: los getters pasan a getName(), getBasePrice()... (Lombok)
 * y salen los 2 tests de rehydrate, porque JPA reconstruye la entidad desde la base.
 * Sin Spring, solo JUnit 5.
 */
class ShippingServiceTest {

    private static final Money PRECIO_BASE = Money.of(new BigDecimal("4990"), "CLP");

    @Test
    void deberiaCrearServicioConDatosValidos() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 10);

        assertEquals("Express", service.getName());
        assertEquals(PRECIO_BASE, service.getBasePrice());
        assertEquals(10, service.getAvailableCapacity());
        assertTrue(service.isActive());
        assertNotNull(service.getId());
        assertEquals(36, service.getId().length());   // UUID, como generaba ServiceId
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
        assertEquals(2, service.getAvailableCapacity());

        service.decreaseCapacity();
        assertEquals(1, service.getAvailableCapacity());
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
        assertEquals(8, service.getAvailableCapacity());
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
        assertEquals(nuevo, service.getBasePrice());
    }

    @Test
    void deberiaDesactivarYActivar() {
        ShippingService service = ShippingService.create("Express", PRECIO_BASE, 3);

        service.deactivate();
        assertFalse(service.isActive());

        service.activate();
        assertTrue(service.isActive());
    }
}

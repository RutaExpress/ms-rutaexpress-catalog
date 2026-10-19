package com.rutaexpress.catalog.domain.model;

import com.rutaexpress.catalog.domain.exception.CapacityExhaustedException;

import java.util.Objects;

/**
 * Aggregate Root del dominio catalog.
 * Representa un servicio de envío con su tarifa base y capacidad de flota disponible.
 *
 * Regla de negocio clave: la capacidad no puede quedar negativa al aceptar envíos.
 */
public class ShippingService {

    private final ServiceId id;
    private String name;
    private Money basePrice;
    private int availableCapacity;
    private boolean active;

    private ShippingService(ServiceId id, String name, Money basePrice, int availableCapacity, boolean active) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio");
        }
        if (basePrice == null) {
            throw new IllegalArgumentException("La tarifa base es obligatoria");
        }
        if (availableCapacity < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa");
        }
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.availableCapacity = availableCapacity;
        this.active = active;
    }

    /** Factory para crear un servicio nuevo. */
    public static ShippingService create(String name, Money basePrice, int initialCapacity) {
        return new ShippingService(ServiceId.generate(), name, basePrice, initialCapacity, true);
    }

    /** Factory para reconstruir un servicio desde persistencia. */
    public static ShippingService rehydrate(ServiceId id, String name, Money basePrice, int availableCapacity, boolean active) {
        return new ShippingService(id, name, basePrice, availableCapacity, active);
    }

    /**
     * Reduce la capacidad en 1 cuando se acepta un envío.
     * @throws CapacityExhaustedException si no hay capacidad disponible.
     */
    public void decreaseCapacity() {
        if (!active) {
            throw new IllegalStateException("No se puede operar sobre un servicio inactivo");
        }
        if (availableCapacity <= 0) {
            throw new CapacityExhaustedException("No hay capacidad disponible para el servicio " + name);
        }
        this.availableCapacity--;
    }

    /** Aumenta la capacidad cuando se libera un vehículo o se cancela un envío. */
    public void increaseCapacity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El incremento debe ser positivo");
        }
        this.availableCapacity += amount;
    }

    public void updatePrice(Money newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("La nueva tarifa no puede ser nula");
        }
        this.basePrice = newPrice;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public ServiceId id() { return id; }
    public String name() { return name; }
    public Money basePrice() { return basePrice; }
    public int availableCapacity() { return availableCapacity; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShippingService that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ShippingService{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", availableCapacity=" + availableCapacity +
                ", active=" + active +
                '}';
    }
}
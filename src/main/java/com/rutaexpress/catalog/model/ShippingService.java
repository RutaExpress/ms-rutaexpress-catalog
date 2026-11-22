package com.rutaexpress.catalog.model;

import java.util.Objects;
import java.util.UUID;

import com.rutaexpress.catalog.exception.CapacityExhaustedException;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Servicio de envío con su tarifa base y capacidad de flota disponible.
 * En capas, la entidad JPA es a la vez el modelo (guía, sección 8): reemplaza a ShippingService del dominio,
 * ServiceJpaEntity y ServiceMapper. Las reglas de negocio de Yanfranco se mantienen aquí.
 *
 * Regla clave: la capacidad no puede quedar negativa al aceptar envíos.
 */
@Entity
@Table(name = "shipping_services")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // constructor vacío para JPA, sin abrirlo al resto
public class ShippingService {

    @Id
    @Column(length = 36)
    private String id;                      // UUID en texto (antes el Value Object ServiceId)

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Embedded
    private Money basePrice;                // columnas base_price y currency

    @Column(name = "available_capacity", nullable = false)
    private int availableCapacity;

    @Column(nullable = false)
    private boolean active;

    private ShippingService(String id, String name, Money basePrice, int availableCapacity, boolean active) {
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

    /** Factory para crear un servicio nuevo: activo y con id generado. */
    public static ShippingService create(String name, Money basePrice, int initialCapacity) {
        return new ShippingService(UUID.randomUUID().toString(), name, basePrice, initialCapacity, true);
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

    // dos servicios son el mismo si tienen el mismo id (identidad de entidad)
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
}

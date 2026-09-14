package com.rutaexpress.catalog.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Entidad JPA. Representa la tabla SHIPPING_SERVICES.
 * NO es la entidad de dominio, es solo un detalle de persistencia.
 */
@Entity
@Table(name = "shipping_services")
public class ServiceJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "available_capacity", nullable = false)
    private int availableCapacity;

    @Column(name = "active", nullable = false)
    private boolean active;

    // Constructor vacío requerido por JPA
    protected ServiceJpaEntity() {
    }

    public ServiceJpaEntity(String id, String name, BigDecimal basePrice,
                            String currency, int availableCapacity, boolean active) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.currency = currency;
        this.availableCapacity = availableCapacity;
        this.active = active;
    }

    // Getters y setters (JPA los necesita)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public int getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(int availableCapacity) { this.availableCapacity = availableCapacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
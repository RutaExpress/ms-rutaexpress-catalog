package com.rutaexpress.catalog.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Value Object que representa un monto de dinero con su moneda (de Yanfranco).
 * En capas se conserva como @Embeddable: sus 2 campos se guardan como columnas de la tabla de ShippingService
 * (no es una tabla aparte). Se usa BigDecimal para evitar errores de precisión con double/float.
 */
@Embeddable
public class Money {

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    // constructor vacío: lo exige JPA para armar el objeto al leer la tabla
    protected Money() {
    }

    private Money(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser nulo ni negativo");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money ofClp(BigDecimal amount) {
        return new Money(amount, "CLP");
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currency() {
        return currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("No se pueden sumar montos de distintas monedas");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}

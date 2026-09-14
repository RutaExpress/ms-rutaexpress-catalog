package com.rutaexpress.catalog;

import org.junit.jupiter.api.Test;

import com.rutaexpress.catalog.domain.model.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitarios del Value Object Money.
 * Sin Spring, solo JUnit 5 puro.
 */
class MoneyTest {

    @Test
    void deberiaCrearMoneyValido() {
        Money money = Money.of(new BigDecimal("4990"), "CLP");
        assertEquals(new BigDecimal("4990"), money.amount());
        assertEquals("CLP", money.currency());
    }

    @Test
    void deberiaCrearMoneyConFactoryClp() {
        Money money = Money.ofClp(new BigDecimal("1500"));
        assertEquals("CLP", money.currency());
    }

    @Test
    void deberiaFallarSiMontoEsNegativo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of(new BigDecimal("-1"), "CLP")
        );
        assertEquals("El monto no puede ser nulo ni negativo", ex.getMessage());
    }

    @Test
    void deberiaFallarSiMonedaEsVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of(BigDecimal.TEN, ""));
    }

    @Test
    void deberiaSumarMontosDeLaMismaMoneda() {
        Money a = Money.of(new BigDecimal("1000"), "CLP");
        Money b = Money.of(new BigDecimal("500"), "CLP");
        Money result = a.add(b);
        assertEquals(new BigDecimal("1500"), result.amount());
        assertEquals("CLP", result.currency());
    }

    @Test
    void deberiaFallarAlSumarMontosDeDistintasMonedas() {
        Money clp = Money.of(new BigDecimal("1000"), "CLP");
        Money usd = Money.of(new BigDecimal("10"), "USD");
        assertThrows(IllegalArgumentException.class, () -> clp.add(usd));
    }

    @Test
    void deberiaSerIgualPorValor() {
        Money a = Money.of(new BigDecimal("1000.00"), "CLP");
        Money b = Money.of(new BigDecimal("1000"), "CLP");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void deberiaSerDistintoSiCambiaLaMoneda() {
        Money clp = Money.of(new BigDecimal("1000"), "CLP");
        Money usd = Money.of(new BigDecimal("1000"), "USD");
        assertNotEquals(clp, usd);
    }
}
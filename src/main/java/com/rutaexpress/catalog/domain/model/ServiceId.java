package com.rutaexpress.catalog.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un servicio de envío.
 * Inmutable, sin dependencias de framework.
 */
public final class ServiceId {

    private final String value;

    private ServiceId(String value) {
        this.value = value;
    }

    public static ServiceId generate() {
        return new ServiceId(UUID.randomUUID().toString());
    }

    public static ServiceId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El ServiceId no puede ser nulo o vacío");
        }
        return new ServiceId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceId serviceId)) return false;
        return Objects.equals(value, serviceId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
package com.rutaexpress.catalog.domain.exception;

/**
 * Excepción base de la capa de dominio.
 * Extiende RuntimeException porque representa violaciones de reglas de negocio,
 * no errores técnicos recuperables.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
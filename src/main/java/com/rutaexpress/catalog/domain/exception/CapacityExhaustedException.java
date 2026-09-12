package com.rutaexpress.catalog.domain.exception;

/**
 * Excepción de dominio: se lanza cuando se intenta aceptar un envío
 * pero no hay capacidad disponible en la flota del servicio.
 */
public class CapacityExhaustedException extends DomainException {

    public CapacityExhaustedException(String message) {
        super(message);
    }
}
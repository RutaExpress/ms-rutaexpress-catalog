package com.rutaexpress.catalog.exception;

// excepción de negocio: no queda capacidad para aceptar el envío (409)
public class CapacityExhaustedException extends RuntimeException {

    public CapacityExhaustedException(String message) {
        super(message);
    }
}

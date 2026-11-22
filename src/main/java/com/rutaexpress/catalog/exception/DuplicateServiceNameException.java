package com.rutaexpress.catalog.exception;

// excepción de negocio: ya existe un servicio con ese nombre (409; antes IllegalArgumentException → 400)
public class DuplicateServiceNameException extends RuntimeException {

    public DuplicateServiceNameException(String name) {
        super("Ya existe un servicio con ese nombre: " + name);
    }
}

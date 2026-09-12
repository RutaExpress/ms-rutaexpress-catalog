package com.rutaexpress.catalog.domain.exception;

/**
 * Excepción de dominio: el servicio solicitado no existe.
 */
public class ServiceNotFoundException extends DomainException {

    public ServiceNotFoundException(String id) {
        super("Servicio no encontrado con id: " + id);
    }
}
package com.rutaexpress.catalog.exception;

// excepción de negocio: el servicio pedido no existe (404)
public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String id) {
        super("Servicio no encontrado con id: " + id);
    }
}

package com.rutaexpress.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// errores HTTP de catalog con ProblemDetail (contrato 5); antes un Map propio con timestamp, status y message
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: el servicio no existe
    @ExceptionHandler(ServiceNotFoundException.class)
    public ProblemDetail handleNotFound(ServiceNotFoundException ex) {
        return problema(HttpStatus.NOT_FOUND, "Servicio no encontrado", ex.getMessage());
    }

    // 409: sin capacidad, nombre repetido u operación sobre un servicio inactivo
    @ExceptionHandler({ CapacityExhaustedException.class, DuplicateServiceNameException.class, IllegalStateException.class })
    public ProblemDetail handleConflict(RuntimeException ex) {
        return problema(HttpStatus.CONFLICT, "Conflicto", ex.getMessage());
    }

    // 400: falla un @NotBlank / @NotNull / @Min / @PositiveOrZero del DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Datos inválidos");
        return problema(HttpStatus.BAD_REQUEST, "Solicitud inválida", detalle);
    }

    // 400: regla de negocio con un dato inválido (por ejemplo, capacidad a sumar negativa)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage());
    }

    // 400: el cuerpo no es un JSON válido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Solicitud inválida", "El cuerpo no es un JSON válido");
    }

    // 500: cualquier otro error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Error interno en catalog: " + ex.getMessage());
    }

    // problema: arma el ProblemDetail (Spring Framework 7 pide HttpStatusCode, no un int)
    private ProblemDetail problema(HttpStatus estado, String titulo, String detalle) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(estado, detalle);
        problem.setTitle(titulo);
        return problem;
    }
}

package com.rutaexpress.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rutaexpress.catalog.dto.ServiceResponse;
import com.rutaexpress.catalog.exception.CapacityExhaustedException;
import com.rutaexpress.catalog.exception.ServiceNotFoundException;
import com.rutaexpress.catalog.service.CatalogService;

// test de la capa web de catalog: ruta /api/v1 (contrato 3) y errores con ProblemDetail (contrato 5)
@WebMvcTest(ServiceController.class)
class ServiceControllerTest {

    private static final String RUTA = "/api/v1/catalog/services";

    // simulador de peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // dependencia simulada (servicio de catálogo)
    @MockitoBean
    private CatalogService service;

    // test 1 (regla principal, HU de catálogo): crear un servicio -> 201 con el servicio creado
    @Test
    @DisplayName("POST /api/v1/catalog/services: 201")
    void createGets201() throws Exception {
        when(service.create(any())).thenReturn(
                new ServiceResponse("id-1", "Express", new BigDecimal("4990"), "CLP", 10, true));

        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Express\",\"basePrice\":4990,\"currency\":\"CLP\",\"initialCapacity\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"))
                .andExpect(jsonPath("$.availableCapacity").value(10));
    }

    // test 2 (validación): sin nombre -> 400 y no se llama al service
    @Test
    @DisplayName("POST sin nombre: 400 sin llamar al service")
    void blankNameGets400() throws Exception {
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"basePrice\":4990,\"initialCapacity\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Solicitud inválida"));
        verifyNoInteractions(service);
    }

    // test 3 (caso de error): servicio que no existe -> 404 con ProblemDetail
    @Test
    @DisplayName("GET /{id} inexistente: 404")
    void unknownServiceGets404() throws Exception {
        when(service.findById("no-existe")).thenThrow(new ServiceNotFoundException("no-existe"));

        mockMvc.perform(get(RUTA + "/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Servicio no encontrado"));
    }

    // test 4 (regla de negocio): bajar capacidad sin cupo -> 409
    @Test
    @DisplayName("PUT sin capacidad: 409")
    void capacityExhaustedGets409() throws Exception {
        when(service.update(eq("id-1"), any())).thenThrow(new CapacityExhaustedException("No hay capacidad disponible para el servicio Express"));

        mockMvc.perform(put(RUTA + "/id-1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"capacityDelta\":-1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflicto"));
    }
}

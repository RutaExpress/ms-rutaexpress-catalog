package com.rutaexpress.catalog.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA.
 * Solo conoce ServiceJpaEntity, NO el dominio.
 */
public interface ServiceJpaRepository extends JpaRepository<ServiceJpaEntity, String> {

    List<ServiceJpaEntity> findByActiveTrue();

    boolean existsByName(String name);
}
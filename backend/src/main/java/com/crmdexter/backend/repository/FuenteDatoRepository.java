package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.FuenteDato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FuenteDatoRepository extends JpaRepository<FuenteDato, UUID> {
    List<FuenteDato> findByProyectoId(UUID proyectoId);
}

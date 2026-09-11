package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.EjecucionProcesamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EjecucionProcesamientoRepository extends JpaRepository<EjecucionProcesamiento, UUID> {
    List<EjecucionProcesamiento> findByProyectoIdOrderByCreatedAtDesc(UUID proyectoId);
}

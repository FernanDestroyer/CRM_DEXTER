package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatasetRepository extends JpaRepository<Dataset, UUID> {
    List<Dataset> findByProyectoIdOrderByFechaCargaDesc(UUID proyectoId);
    List<Dataset> findByFuenteId(UUID fuenteId);
}

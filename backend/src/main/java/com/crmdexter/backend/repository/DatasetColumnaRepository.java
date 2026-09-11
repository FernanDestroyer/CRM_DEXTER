package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.DatasetColumna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatasetColumnaRepository extends JpaRepository<DatasetColumna, UUID> {
    List<DatasetColumna> findByDatasetIdOrderByPosicion(UUID datasetId);
    List<DatasetColumna> findByDatasetIdIn(List<UUID> datasetIds);
}

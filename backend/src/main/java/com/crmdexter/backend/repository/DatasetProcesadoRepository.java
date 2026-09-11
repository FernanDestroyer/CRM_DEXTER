package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.DatasetProcesado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatasetProcesadoRepository extends JpaRepository<DatasetProcesado, UUID> {
    List<DatasetProcesado> findByProyectoIdOrderByVersionDesc(UUID proyectoId);
}

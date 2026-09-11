package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {
    List<Proyecto> findByCreatedByOrderByCreatedAtDesc(UUID createdBy);
}

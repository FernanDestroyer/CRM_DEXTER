package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.EsquemaCanonico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EsquemaCanonicoRepository extends JpaRepository<EsquemaCanonico, UUID> {
    List<EsquemaCanonico> findByProyectoId(UUID proyectoId);
    Optional<EsquemaCanonico> findFirstByProyectoIdAndActivoTrueOrderByVersionDesc(UUID proyectoId);
}

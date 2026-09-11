package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.EsquemaCampo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EsquemaCampoRepository extends JpaRepository<EsquemaCampo, UUID> {
    List<EsquemaCampo> findByEsquemaIdOrderByPosicion(UUID esquemaId);
}

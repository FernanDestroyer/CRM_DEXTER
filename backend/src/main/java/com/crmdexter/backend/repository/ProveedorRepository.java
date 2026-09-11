package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProveedorRepository extends JpaRepository<Proveedor, UUID> {
    Optional<Proveedor> findByOrganizacionIdAndCodigo(UUID organizacionId, String codigo);
}

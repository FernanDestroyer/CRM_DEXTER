package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.SolicitudAcceso;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SolicitudAccesoRepository extends JpaRepository<SolicitudAcceso, UUID> {

    boolean existsByEmailSolicitanteAndEstado(String emailSolicitante, String estado);
    
    Optional<SolicitudAcceso> findTopByEmailSolicitanteOrderByCreatedAtDesc(String emailSolicitante);

    List<SolicitudAcceso> findByEstadoOrderByCreatedAtAsc(String estado);

    Optional<SolicitudAcceso> findTopByEmailSolicitanteIgnoreCaseOrderByCreatedAtDesc(String emailSolicitante);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) " +
           "AND UPPER(u.rol) = 'ADMINISTRADOR' AND UPPER(u.estado) = 'ACTIVO'")
    long countActiveAdministratorsByEmail(String email);
}

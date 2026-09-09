package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.SolicitudAcceso;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolicitudAccesoRepository extends JpaRepository<SolicitudAcceso, UUID> {

    boolean existsByEmailSolicitanteAndEstado(String emailSolicitante, String estado);
    
    Optional<SolicitudAcceso> findTopByEmailSolicitanteOrderByCreatedAtDesc(String emailSolicitante);

    @Query(value=  "SELECT \n" + //
                "\tCASE WHEN email= :email THEN 1 ELSE 0 END as EsAdmin\n" + //
                "FROM usuarios;", nativeQuery = true)
    int isAdmin(String email);
}


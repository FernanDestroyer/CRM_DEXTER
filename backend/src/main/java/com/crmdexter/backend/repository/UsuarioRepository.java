package com.crmdexter.backend.repository;

import com.crmdexter.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmailIgnoreCaseAndRolAndEstado(String email, String rol, String estado);
}

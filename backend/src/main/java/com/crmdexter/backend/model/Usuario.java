package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "organizacion_id", nullable = false, columnDefinition = "BINARY(16)") private UUID organizacionId;
    @Column(nullable = false, length = 150) private String nombre;
    @Column(nullable = false, length = 180, unique = true) private String email;
    @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
    @Column(nullable = false, length = 40) private String rol = "ANALISTA";
    @Column(nullable = false, length = 20) private String estado = "ACTIVO";
    @Column(name = "ultimo_acceso") private LocalDateTime ultimoAcceso;
    protected Usuario() {}

    public UUID getId() { return id; }
    public UUID getOrganizacionId() { return organizacionId; }
    public String getEmail() { return email; }
}

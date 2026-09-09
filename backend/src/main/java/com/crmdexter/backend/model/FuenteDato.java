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

@Entity @Table(name = "fuentes_datos")
public class FuenteDato {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proyecto_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proyectoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proveedor_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proveedorId;
    @Column(nullable = false, length = 150) private String nombre;
    @Column(length = 60) private String codigo;
    @Column(name = "tipo_fuente", nullable = false, length = 30) private String tipoFuente = "CSV";
    @Column(length = 500) private String descripcion;
    protected FuenteDato() {}
}

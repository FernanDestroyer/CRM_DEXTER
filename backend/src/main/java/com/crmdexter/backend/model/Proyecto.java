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

@Entity @Table(name = "proyectos")
public class Proyecto {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "organizacion_id", nullable = false, columnDefinition = "BINARY(16)") private UUID organizacionId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "created_by", nullable = false, columnDefinition = "BINARY(16)") private UUID createdBy;
    @Column(nullable = false, length = 180) private String nombre;
    @Column(length = 60) private String codigo;
    @Column(length = 1000) private String descripcion;
    @Column(name = "tipo_dato", nullable = false, length = 40) private String tipoDato;
    @Column(nullable = false, length = 30) private String estado = "CREADO";
    protected Proyecto() {}
}

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

@Entity @Table(name = "dashboards")
public class Dashboard {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proyecto_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proyectoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "dataset_procesado_id", columnDefinition = "BINARY(16)") private UUID datasetProcesadoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "created_by", nullable = false, columnDefinition = "BINARY(16)") private UUID createdBy;
    @Column(nullable = false, length = 180) private String nombre;
    @Column(length = 500) private String descripcion;
    @Column(nullable = false) private boolean activo = true;
    protected Dashboard() {}

    public UUID getId() { return id; }
    public UUID getProyectoId() { return proyectoId; }
    public String getNombre() { return nombre; }
}

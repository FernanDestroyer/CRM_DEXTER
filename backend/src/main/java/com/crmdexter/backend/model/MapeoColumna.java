package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "mapeos_columnas")
public class MapeoColumna {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "dataset_columna_id", nullable = false, columnDefinition = "BINARY(16)") private UUID datasetColumnaId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "esquema_campo_id", nullable = false, columnDefinition = "BINARY(16)") private UUID esquemaCampoId;
    @Column(nullable = false, length = 30) private String metodo;
    @Column(nullable = false, precision = 6, scale = 5) private BigDecimal confianza = BigDecimal.ZERO;
    @Column(nullable = false, length = 20) private String estado = "SUGERIDO";
    @Column(name = "es_confirmado", nullable = false) private boolean esConfirmado;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "confirmado_por", columnDefinition = "BINARY(16)") private UUID confirmadoPor;
    protected MapeoColumna() {}

    public UUID getId() { return id; }
    public UUID getDatasetColumnaId() { return datasetColumnaId; }
    public UUID getEsquemaCampoId() { return esquemaCampoId; }
    public String getEstado() { return estado; }
    public boolean isEsConfirmado() { return esConfirmado; }
}

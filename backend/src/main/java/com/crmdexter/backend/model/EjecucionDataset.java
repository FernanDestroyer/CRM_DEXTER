package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "ejecucion_datasets")
public class EjecucionDataset {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "ejecucion_id", nullable = false, columnDefinition = "BINARY(16)") private UUID ejecucionId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "dataset_id", nullable = false, columnDefinition = "BINARY(16)") private UUID datasetId;
    @Column(name = "filas_procesadas", nullable = false) private long filasProcesadas;
    protected EjecucionDataset() {}
}

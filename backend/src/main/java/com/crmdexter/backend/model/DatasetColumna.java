package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "dataset_columnas")
public class DatasetColumna {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(columnDefinition = "BINARY(16)") private UUID id;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "dataset_id", nullable = false, columnDefinition = "BINARY(16)") private UUID datasetId;
    @Column(name = "nombre_original", nullable = false, length = 255) private String nombreOriginal;
    @Column(name = "nombre_normalizado", nullable = false, length = 255) private String nombreNormalizado;
    @Column(name = "tipo_detectado", nullable = false, length = 30) private String tipoDetectado;
    @Column(nullable = false) private int posicion;
    @Column(name = "es_obligatoria", nullable = false) private boolean esObligatoria;
    @Column(name = "total_nulos", nullable = false) private long totalNulos;
    @Column(name = "total_unicos", nullable = false) private long totalUnicos;
    @Column(name = "porcentaje_nulos", nullable = false, precision = 7, scale = 4) private BigDecimal porcentajeNulos = BigDecimal.ZERO;
    @Column(name = "ejemplo_valor", length = 500) private String ejemploValor;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "estadisticas_json", columnDefinition = "json") private Map<String, Object> estadisticasJson;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    protected DatasetColumna() {}

    public DatasetColumna(UUID datasetId, String nombreOriginal, String nombreNormalizado, String tipoDetectado,
                           int posicion, long totalNulos, long totalUnicos, BigDecimal porcentajeNulos,
                           String ejemploValor) {
        this.datasetId = datasetId;
        this.nombreOriginal = nombreOriginal;
        this.nombreNormalizado = nombreNormalizado;
        this.tipoDetectado = tipoDetectado;
        this.posicion = posicion;
        this.totalNulos = totalNulos;
        this.totalUnicos = totalUnicos;
        this.porcentajeNulos = porcentajeNulos;
        this.ejemploValor = ejemploValor;
    }

    public UUID getId() { return id; }
    public UUID getDatasetId() { return datasetId; }
    public String getNombreOriginal() { return nombreOriginal; }
    public String getNombreNormalizado() { return nombreNormalizado; }
    public String getTipoDetectado() { return tipoDetectado; }
    public int getPosicion() { return posicion; }
    public long getTotalNulos() { return totalNulos; }
    public long getTotalUnicos() { return totalUnicos; }
    public BigDecimal getPorcentajeNulos() { return porcentajeNulos; }
    public String getEjemploValor() { return ejemploValor; }
}

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

@Entity @Table(name = "datasets_procesados")
public class DatasetProcesado {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proyecto_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proyectoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "ejecucion_id", nullable = false, columnDefinition = "BINARY(16)") private UUID ejecucionId;
    @Column(name = "nombre_archivo", nullable = false, length = 255) private String nombreArchivo;
    @Column(name = "ruta_archivo", nullable = false, length = 1000) private String rutaArchivo;
    @Column(nullable = false, length = 20) private String formato = "PARQUET";
    @Column(name = "hash_archivo", length = 64) private String hashArchivo;
    @Column(name = "tamano_bytes", nullable = false) private long tamanoBytes;
    @Column(name = "numero_filas", nullable = false) private long numeroFilas;
    @Column(name = "numero_columnas", nullable = false) private int numeroColumnas;
    @Column(nullable = false) private int version = 1;
    protected DatasetProcesado() {}

    public DatasetProcesado(UUID proyectoId, UUID ejecucionId, String nombreArchivo, String rutaArchivo,
                             long numeroFilas, int numeroColumnas, int version) {
        this.proyectoId = proyectoId;
        this.ejecucionId = ejecucionId;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.numeroFilas = numeroFilas;
        this.numeroColumnas = numeroColumnas;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getProyectoId() { return proyectoId; }
    public UUID getEjecucionId() { return ejecucionId; }
    public String getNombreArchivo() { return nombreArchivo; }
    public String getRutaArchivo() { return rutaArchivo; }
    public String getFormato() { return formato; }
    public long getNumeroFilas() { return numeroFilas; }
    public int getNumeroColumnas() { return numeroColumnas; }
    public int getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

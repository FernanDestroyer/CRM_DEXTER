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

@Entity @Table(name = "datasets")
public class Dataset {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proyecto_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proyectoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "fuente_id", nullable = false, columnDefinition = "BINARY(16)") private UUID fuenteId;
    @Column(name = "nombre_original", nullable = false, length = 255) private String nombreOriginal;
    @Column(name = "nombre_almacenado", nullable = false, length = 255) private String nombreAlmacenado;
    @Column(name = "ruta_archivo", nullable = false, length = 1000) private String rutaArchivo;
    @Column(name = "hash_archivo", length = 64) private String hashArchivo;
    @Column(nullable = false, length = 20) private String formato = "CSV";
    @Column(name = "tamano_bytes", nullable = false) private long tamanoBytes;
    @Column(name = "filas_detectadas", nullable = false) private long filasDetectadas;
    @Column(nullable = false, length = 30) private String estado = "RECIBIDO";
    @CreationTimestamp @Column(name = "fecha_carga", nullable = false, updatable = false) private LocalDateTime fechaCarga;
    protected Dataset() {}

    public Dataset(UUID proyectoId, UUID fuenteId, String nombreOriginal, String nombreAlmacenado,
                    String rutaArchivo, String hashArchivo, String formato, long tamanoBytes,
                    long filasDetectadas, String estado) {
        this.proyectoId = proyectoId;
        this.fuenteId = fuenteId;
        this.nombreOriginal = nombreOriginal;
        this.nombreAlmacenado = nombreAlmacenado;
        this.rutaArchivo = rutaArchivo;
        this.hashArchivo = hashArchivo;
        this.formato = formato;
        this.tamanoBytes = tamanoBytes;
        this.filasDetectadas = filasDetectadas;
        this.estado = estado;
    }

    public UUID getId() { return id; }
    public UUID getProyectoId() { return proyectoId; }
    public UUID getFuenteId() { return fuenteId; }
    public String getNombreOriginal() { return nombreOriginal; }
    public String getNombreAlmacenado() { return nombreAlmacenado; }
    public String getRutaArchivo() { return rutaArchivo; }
    public String getHashArchivo() { return hashArchivo; }
    public String getFormato() { return formato; }
    public long getTamanoBytes() { return tamanoBytes; }
    public long getFilasDetectadas() { return filasDetectadas; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
}

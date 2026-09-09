package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "ejecuciones_procesamiento")
public class EjecucionProcesamiento {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(columnDefinition = "BINARY(16)") private UUID id;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "proyecto_id", nullable = false, columnDefinition = "BINARY(16)") private UUID proyectoId;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "ejecutado_por", nullable = false, columnDefinition = "BINARY(16)") private UUID ejecutadoPor;
    @Column(name = "tipo_ejecucion", nullable = false, length = 30) private String tipoEjecucion;
    @Column(nullable = false, length = 20) private String estado = "PENDIENTE";
    @Column(name = "filas_entrada", nullable = false) private long filasEntrada;
    @Column(name = "filas_salida", nullable = false) private long filasSalida;
    @Column(name = "mensaje_error", columnDefinition = "TEXT") private String mensajeError;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    protected EjecucionProcesamiento() {}
}

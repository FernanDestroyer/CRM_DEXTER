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

@Entity @Table(name = "esquema_campos")
public class EsquemaCampo {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "esquema_id", nullable = false, columnDefinition = "BINARY(16)") private UUID esquemaId;
    @Column(name = "nombre_canonico", nullable = false, length = 150) private String nombreCanonico;
    @Column(nullable = false, length = 180) private String etiqueta;
    @Column(name = "tipo_dato", nullable = false, length = 30) private String tipoDato;
    @Column(length = 500) private String descripcion;
    @Column(name = "es_obligatorio", nullable = false) private boolean esObligatorio;
    @Column(nullable = false) private int posicion;
    protected EsquemaCampo() {}
}

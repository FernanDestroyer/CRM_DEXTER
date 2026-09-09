package com.crmdexter.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "widgets_dashboard")
public class WidgetDashboard {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "dashboard_id", nullable = false, columnDefinition = "BINARY(16)") private UUID dashboardId;
    @Column(nullable = false, length = 180) private String titulo;
    @Column(name = "tipo_widget", nullable = false, length = 30) private String tipoWidget;
    @Column(name = "posicion_x", nullable = false) private int posicionX;
    @Column(name = "posicion_y", nullable = false) private int posicionY;
    @Column(nullable = false) private int ancho = 4;
    @Column(nullable = false) private int alto = 3;
    @Column(nullable = false) private int orden;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "configuracion_json", nullable = false, columnDefinition = "json") private Map<String, Object> configuracionJson;
    protected WidgetDashboard() {}
}

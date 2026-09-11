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

/** Represents an email access request awaiting an administrator decision. */
@Entity
@Table(name = "solicitudes_acceso")
public class SolicitudAcceso {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @JdbcTypeCode(SqlTypes.BINARY) @Column(name = "id", columnDefinition = "BINARY(16)") private UUID id;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @Column(name = "email_solicitante", nullable = false, length = 180)
    private String emailSolicitante;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "notificacion_leida", nullable = false)
    private boolean notificacionLeida = false;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "aprobado_por", columnDefinition = "BINARY(16)", nullable = true)
    private UUID aprobadoPor;

    @Column(name = "decidido_en", nullable = true)
    private LocalDateTime decididoEn;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    // Store only a one-way hash; the four-digit OTP itself must never be persisted.
    @Column(name = "otp_hash", length = 255)
    private String otpHash;

    @Column(name = "otp_expira_en", nullable = true)
    private LocalDateTime otpExpiraEn;

    @Column(name = "intentos_otp", nullable = false)
    private int intentosOtp = 0;

    public SolicitudAcceso() {}

    public String getEmailSolicitante() { return emailSolicitante; }
    public void setEmailSolicitante(String emailSolicitante) { this.emailSolicitante = emailSolicitante; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isNotificacionLeida() { return notificacionLeida; }
    public void setNotificacionLeida(boolean notificacionLeida) { this.notificacionLeida = notificacionLeida; }
    public int getIntentosOtp() { return intentosOtp; }
    public void setIntentosOtp(int intentosOtp) { this.intentosOtp = intentosOtp; }
    public UUID getId() { return id; }
    public UUID getAprobadoPor() { return aprobadoPor; }
    public LocalDateTime getDecididoEn() { return decididoEn; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public String getOtpHash() { return otpHash; }
    public LocalDateTime getOtpExpiraEn() { return otpExpiraEn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setAprobadoPor(UUID aprobadoPor) { this.aprobadoPor = aprobadoPor; }
    public void setDecididoEn(LocalDateTime decididoEn) { this.decididoEn = decididoEn; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }
    public void setOtpExpiraEn(LocalDateTime otpExpiraEn) { this.otpExpiraEn = otpExpiraEn; }
}

package com.crmdexter.backend.dto;

import com.crmdexter.backend.model.Proyecto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ProyectoDto {
    private ProyectoDto() {}

    public static class CrearRequest {
        private String nombre;
        private String rubro;
        private String descripcion;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getRubro() { return rubro; }
        public void setRubro(String rubro) { this.rubro = rubro; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    public record Response(
        String id,
        @JsonProperty("usuario_propietario_id") String usuarioPropietarioId,
        @JsonProperty("empresa_id") String empresaId,
        String nombre,
        String rubro,
        String descripcion,
        String estado,
        @JsonProperty("creado_en") LocalDateTime creadoEn,
        @JsonProperty("actualizado_en") LocalDateTime actualizadoEn
    ) {
        public static Response from(Proyecto proyecto) {
            return new Response(
                proyecto.getId().toString(),
                proyecto.getCreatedBy().toString(),
                null,
                proyecto.getNombre(),
                proyecto.getTipoDato(),
                proyecto.getDescripcion(),
                proyecto.getEstado().toLowerCase(),
                proyecto.getCreatedAt(),
                proyecto.getUpdatedAt()
            );
        }
    }
}

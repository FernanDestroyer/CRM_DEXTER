package com.crmdexter.backend.dto;

import com.crmdexter.backend.model.FuenteDato;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public final class FusionDto {
    private FusionDto() {}

    public record FuenteResponse(
        String id,
        String nombre,
        String codigo,
        @JsonProperty("tipo_fuente") String tipoFuente,
        @JsonProperty("total_datasets") long totalDatasets,
        @JsonProperty("total_filas") long totalFilas,
        String estado
    ) {
        public static FuenteResponse from(FuenteDato fuente, long totalDatasets, long totalFilas, boolean conAlertas) {
            return new FuenteResponse(
                fuente.getId().toString(),
                fuente.getNombre(),
                fuente.getCodigo(),
                fuente.getTipoFuente(),
                totalDatasets,
                totalFilas,
                conAlertas ? "con_alertas" : "listo"
            );
        }
    }

    public static class EjecutarRequest {
        @JsonProperty("fuente_ids")
        private List<String> fuenteIds;
        private String estrategia;

        public List<String> getFuenteIds() { return fuenteIds; }
        public void setFuenteIds(List<String> fuenteIds) { this.fuenteIds = fuenteIds; }
        public String getEstrategia() { return estrategia; }
        public void setEstrategia(String estrategia) { this.estrategia = estrategia; }
    }

    public record EjecucionResponse(
        String id,
        String estado,
        @JsonProperty("filas_entrada") long filasEntrada,
        @JsonProperty("filas_salida") long filasSalida,
        @JsonProperty("dataset_procesado_id") String datasetProcesadoId,
        @JsonProperty("creado_en") LocalDateTime creadoEn
    ) {}
}

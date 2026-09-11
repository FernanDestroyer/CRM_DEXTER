package com.crmdexter.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public final class AnalyticsDto {
    private AnalyticsDto() {}

    public record ResumenResponse(
        @JsonProperty("total_datasets") long totalDatasets,
        @JsonProperty("total_filas_cargadas") long totalFilasCargadas,
        @JsonProperty("total_ejecuciones") long totalEjecuciones,
        @JsonProperty("total_datasets_procesados") long totalDatasetsProcesados,
        @JsonProperty("ultima_ejecucion_en") LocalDateTime ultimaEjecucionEn,
        @JsonProperty("esquema_activo") String esquemaActivo,
        @JsonProperty("total_campos_esquema") long totalCamposEsquema
    ) {}
}

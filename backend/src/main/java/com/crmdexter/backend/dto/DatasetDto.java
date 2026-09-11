package com.crmdexter.backend.dto;

import com.crmdexter.backend.model.Dataset;
import com.crmdexter.backend.model.DatasetColumna;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public final class DatasetDto {
    private DatasetDto() {}

    public record ColumnaResponse(
        String nombre,
        String tipo,
        int posicion,
        @JsonProperty("total_nulos") long totalNulos,
        @JsonProperty("total_unicos") long totalUnicos,
        @JsonProperty("porcentaje_nulos") double porcentajeNulos,
        @JsonProperty("ejemplo_valor") String ejemploValor
    ) {
        public static ColumnaResponse from(DatasetColumna columna) {
            return new ColumnaResponse(
                columna.getNombreNormalizado(),
                columna.getTipoDetectado(),
                columna.getPosicion(),
                columna.getTotalNulos(),
                columna.getTotalUnicos(),
                columna.getPorcentajeNulos().doubleValue(),
                columna.getEjemploValor()
            );
        }
    }

    public record Response(
        String id,
        @JsonProperty("proveedor_nombre") String proveedorNombre,
        @JsonProperty("nombre_original") String nombreOriginal,
        String formato,
        @JsonProperty("tamano_bytes") long tamanoBytes,
        @JsonProperty("filas_detectadas") long filasDetectadas,
        String estado,
        @JsonProperty("fecha_carga") LocalDateTime fechaCarga,
        List<ColumnaResponse> columnas
    ) {
        public static Response from(Dataset dataset, String proveedorNombre, List<DatasetColumna> columnas) {
            return new Response(
                dataset.getId().toString(),
                proveedorNombre,
                dataset.getNombreOriginal(),
                dataset.getFormato(),
                dataset.getTamanoBytes(),
                dataset.getFilasDetectadas(),
                dataset.getEstado(),
                dataset.getFechaCarga(),
                columnas.stream().map(ColumnaResponse::from).toList()
            );
        }
    }
}

package com.crmdexter.backend.service.Dataset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

// Lee un CSV completo, detecta columnas y calcula estadísticas básicas por
// columna (tipo, nulos, únicos, ejemplo). No hace nada con XLSX todavía.
@Service
public class CsvAnalizadorService {

    private static final Set<String> VALORES_NULOS = Set.of("", "null", "n/a", "na", "nulo", "-");
    private static final Set<String> VALORES_BOOLEANOS = Set.of("true", "false", "si", "sí", "no", "1", "0");
    private static final Pattern PATRON_FECHA = Pattern.compile(
        "^\\d{4}-\\d{2}-\\d{2}([ T]\\d{2}:\\d{2}(:\\d{2})?)?$|^\\d{2}/\\d{2}/\\d{4}$");

    public record ColumnaAnalizada(String nombreOriginal, String nombreNormalizado, String tipo,
                                    long totalNulos, long totalUnicos, BigDecimal porcentajeNulos,
                                    String ejemploValor) {}

    public record ResultadoAnalisis(long totalFilas, List<ColumnaAnalizada> columnas) {}

    public ResultadoAnalisis analizar(Reader reader) throws IOException {
        CSVFormat formato = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreSurroundingSpaces(true)
            .setTrim(true)
            .build();

        try (CSVParser parser = new CSVParser(reader, formato)) {
            List<String> encabezados = new ArrayList<>(parser.getHeaderNames());
            List<CSVRecord> filas = parser.getRecords();

            List<ColumnaAnalizada> columnas = new ArrayList<>();
            for (String encabezado : encabezados) {
                columnas.add(analizarColumna(encabezado, filas));
            }
            return new ResultadoAnalisis(filas.size(), columnas);
        }
    }

    private ColumnaAnalizada analizarColumna(String encabezado, List<CSVRecord> filas) {
        long nulos = 0;
        Set<String> unicos = new LinkedHashSet<>();
        boolean todosEnteros = true;
        boolean todosDecimales = true;
        boolean todosFechas = true;
        boolean todosBooleanos = true;
        String ejemplo = null;

        for (CSVRecord fila : filas) {
            String valor = fila.isMapped(encabezado) ? fila.get(encabezado) : "";
            String valorNormalizado = valor == null ? "" : valor.trim();

            if (VALORES_NULOS.contains(valorNormalizado.toLowerCase(Locale.ROOT))) {
                nulos++;
                continue;
            }
            unicos.add(valorNormalizado);
            if (ejemplo == null) ejemplo = valorNormalizado;

            if (todosEnteros && !esEntero(valorNormalizado)) todosEnteros = false;
            if (todosDecimales && !esDecimal(valorNormalizado)) todosDecimales = false;
            if (todosFechas && !PATRON_FECHA.matcher(valorNormalizado).matches()) todosFechas = false;
            if (todosBooleanos && !VALORES_BOOLEANOS.contains(valorNormalizado.toLowerCase(Locale.ROOT))) todosBooleanos = false;
        }

        long conDato = filas.size() - nulos;
        String tipo;
        if (conDato == 0) tipo = "TEXTO";
        else if (todosEnteros) tipo = "ENTERO";
        else if (todosDecimales) tipo = "DECIMAL";
        else if (todosFechas) tipo = "FECHA";
        else if (todosBooleanos) tipo = "BOOLEANO";
        else tipo = "TEXTO";

        BigDecimal porcentajeNulos = filas.isEmpty()
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(nulos * 100.0 / filas.size()).setScale(4, RoundingMode.HALF_UP);

        return new ColumnaAnalizada(encabezado, normalizar(encabezado), tipo, nulos, unicos.size(),
            porcentajeNulos, ejemplo);
    }

    private boolean esEntero(String valor) {
        try {
            Long.parseLong(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean esDecimal(String valor) {
        try {
            Double.parseDouble(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
    }
}

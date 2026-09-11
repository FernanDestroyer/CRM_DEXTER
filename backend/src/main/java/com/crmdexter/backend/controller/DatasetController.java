package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.DatasetDto;
import com.crmdexter.backend.model.*;
import com.crmdexter.backend.repository.*;
import com.crmdexter.backend.service.Dataset.CsvAnalizadorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

// Sección Datasets: sube un CSV, lo guarda en disco y persiste su metadata
// (Proveedor, FuenteDato, Dataset, DatasetColumna). Es la base de la que
// dependen Fusión y Analytics: sin datasets cargados, ambas secciones no
// tienen nada que mostrar.
@RestController
@RequestMapping("/api/proyectos/{proyectoId}/datasets")
public class DatasetController {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;
    private final FuenteDatoRepository fuenteDatoRepository;
    private final DatasetRepository datasetRepository;
    private final DatasetColumnaRepository datasetColumnaRepository;
    private final CsvAnalizadorService csvAnalizadorService;

    @Value("${app.storage.uploads-dir}")
    private String uploadsDir;

    public DatasetController(ProyectoRepository proyectoRepository, UsuarioRepository usuarioRepository,
                              ProveedorRepository proveedorRepository, FuenteDatoRepository fuenteDatoRepository,
                              DatasetRepository datasetRepository, DatasetColumnaRepository datasetColumnaRepository,
                              CsvAnalizadorService csvAnalizadorService) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
        this.fuenteDatoRepository = fuenteDatoRepository;
        this.datasetRepository = datasetRepository;
        this.datasetColumnaRepository = datasetColumnaRepository;
        this.csvAnalizadorService = csvAnalizadorService;
    }

    @GetMapping
    public List<DatasetDto.Response> listar(@PathVariable UUID proyectoId, Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        return datasetRepository.findByProyectoIdOrderByFechaCargaDesc(proyecto.getId()).stream()
            .map(dataset -> {
                FuenteDato fuente = fuenteDatoRepository.findById(dataset.getFuenteId()).orElse(null);
                List<DatasetColumna> columnas = datasetColumnaRepository.findByDatasetIdOrderByPosicion(dataset.getId());
                return DatasetDto.Response.from(dataset, fuente == null ? null : fuente.getNombre(), columnas);
            }).toList();
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetDto.Response subir(@PathVariable UUID proyectoId,
                                      @RequestParam("archivo") MultipartFile archivo,
                                      @RequestParam(value = "proveedor", required = false) String proveedorNombre,
                                      Authentication authentication) throws IOException {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        Usuario usuario = usuarioActual(authentication);

        if (archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo está vacío");
        }
        String nombreOriginal = archivo.getOriginalFilename() == null ? "archivo.csv" : archivo.getOriginalFilename();
        String extension = nombreOriginal.contains(".")
            ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.') + 1).toUpperCase()
            : "CSV";
        if (!extension.equals("CSV")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Por ahora solo se aceptan archivos CSV. XLSX está pendiente de integrar.");
        }

        byte[] contenido = archivo.getBytes();
        String hash = calcularHash(contenido);

        // 1. Proveedor: uno por defecto por organización si no se indica.
        String nombreProveedor = (proveedorNombre == null || proveedorNombre.isBlank())
            ? "Carga manual" : proveedorNombre.trim();
        String codigoProveedor = normalizarCodigo(nombreProveedor);
        Proveedor proveedor = proveedorRepository.findByOrganizacionIdAndCodigo(usuario.getOrganizacionId(), codigoProveedor)
            .orElseGet(() -> proveedorRepository.save(new Proveedor(usuario.getOrganizacionId(), nombreProveedor, codigoProveedor)));

        // 2. Fuente de datos para este proyecto.
        FuenteDato fuente = new FuenteDato(proyecto.getId(), proveedor.getId(), nombreProveedor,
            codigoProveedor + "-" + System.currentTimeMillis(), "CSV");
        fuente = fuenteDatoRepository.save(fuente);

        // 3. Guardar el archivo físico.
        Path carpetaProyecto = Path.of(uploadsDir, proyecto.getId().toString());
        Files.createDirectories(carpetaProyecto);
        String nombreAlmacenado = System.currentTimeMillis() + "_" + nombreOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path rutaDestino = carpetaProyecto.resolve(nombreAlmacenado);
        Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        // 4. Analizar el CSV: filas y columnas.
        CsvAnalizadorService.ResultadoAnalisis analisis;
        try (var reader = new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8)) {
            analisis = csvAnalizadorService.analizar(reader);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el CSV: " + e.getMessage());
        }

        Dataset dataset = new Dataset(proyecto.getId(), fuente.getId(), nombreOriginal, nombreAlmacenado,
            rutaDestino.toString(), hash, "CSV", contenido.length, analisis.totalFilas(), "ANALIZADO");
        dataset = datasetRepository.save(dataset);

        int posicion = 0;
        for (CsvAnalizadorService.ColumnaAnalizada columna : analisis.columnas()) {
            datasetColumnaRepository.save(new DatasetColumna(
                dataset.getId(), columna.nombreOriginal(), columna.nombreNormalizado(), columna.tipo(),
                posicion++, columna.totalNulos(), columna.totalUnicos(), columna.porcentajeNulos(),
                columna.ejemploValor()));
        }

        List<DatasetColumna> columnasGuardadas = datasetColumnaRepository.findByDatasetIdOrderByPosicion(dataset.getId());
        return DatasetDto.Response.from(dataset, fuente.getNombre(), columnasGuardadas);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID proyectoId, @PathVariable UUID id, Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        Dataset dataset = datasetRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset no encontrado"));
        if (!dataset.getProyectoId().equals(proyecto.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este dataset no pertenece a este proyecto");
        }
        try {
            Files.deleteIfExists(Path.of(dataset.getRutaArchivo()));
        } catch (IOException ignored) {
            // Si el archivo físico ya no está, igual limpiamos la metadata.
        }
        datasetRepository.delete(dataset);
    }

    private String calcularHash(byte[] contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(contenido));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String normalizarCodigo(String nombre) {
        String base = nombre.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "-").replaceAll("^-+|-+$", "");
        return base.isBlank() ? "PROVEEDOR" : base.length() > 55 ? base.substring(0, 55) : base;
    }

    private Proyecto proyectoDelUsuario(UUID proyectoId, Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
        if (!proyecto.getCreatedBy().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este proyecto");
        }
        return proyecto;
    }

    private Usuario usuarioActual(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}

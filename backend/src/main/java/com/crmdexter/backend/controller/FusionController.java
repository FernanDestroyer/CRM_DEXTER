package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.FusionDto;
import com.crmdexter.backend.model.Dataset;
import com.crmdexter.backend.model.DatasetProcesado;
import com.crmdexter.backend.model.EjecucionProcesamiento;
import com.crmdexter.backend.model.FuenteDato;
import com.crmdexter.backend.model.Proyecto;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.DatasetProcesadoRepository;
import com.crmdexter.backend.repository.DatasetRepository;
import com.crmdexter.backend.repository.EjecucionProcesamientoRepository;
import com.crmdexter.backend.repository.FuenteDatoRepository;
import com.crmdexter.backend.repository.ProyectoRepository;
import com.crmdexter.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

// Sección Fusión y Master: consolida las fuentes de datos de un proyecto.
// Por ahora trabaja solo con metadata (MySQL): cuenta filas y datasets por fuente,
// y registra la ejecución + el dataset_procesado resultante. La lectura y el
// merge real del contenido de los archivos (Parquet/CSV) requiere un lector
// de datos (DuckDB) que todavía no está integrado.
@RestController
@RequestMapping("/api/proyectos/{proyectoId}/fusion")
public class FusionController {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuenteDatoRepository fuenteDatoRepository;
    private final DatasetRepository datasetRepository;
    private final EjecucionProcesamientoRepository ejecucionRepository;
    private final DatasetProcesadoRepository datasetProcesadoRepository;

    public FusionController(ProyectoRepository proyectoRepository, UsuarioRepository usuarioRepository,
                             FuenteDatoRepository fuenteDatoRepository, DatasetRepository datasetRepository,
                             EjecucionProcesamientoRepository ejecucionRepository,
                             DatasetProcesadoRepository datasetProcesadoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fuenteDatoRepository = fuenteDatoRepository;
        this.datasetRepository = datasetRepository;
        this.ejecucionRepository = ejecucionRepository;
        this.datasetProcesadoRepository = datasetProcesadoRepository;
    }

    @GetMapping("/fuentes")
    public List<FusionDto.FuenteResponse> listarFuentes(@PathVariable UUID proyectoId, Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        List<FuenteDato> fuentes = fuenteDatoRepository.findByProyectoId(proyecto.getId());

        return fuentes.stream().map(fuente -> {
            List<Dataset> datasets = datasetRepository.findByFuenteId(fuente.getId());
            long totalFilas = datasets.stream().mapToLong(Dataset::getFilasDetectadas).sum();
            boolean conAlertas = datasets.stream().anyMatch(d -> "ERROR".equals(d.getEstado()));
            return FusionDto.FuenteResponse.from(fuente, datasets.size(), totalFilas, conAlertas);
        }).toList();
    }

    @PostMapping("/ejecutar")
    @ResponseStatus(HttpStatus.CREATED)
    public FusionDto.EjecucionResponse ejecutar(@PathVariable UUID proyectoId,
                                                 @Valid @RequestBody FusionDto.EjecutarRequest request,
                                                 Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        Usuario usuario = usuarioActual(authentication);

        if (request.getFuenteIds() == null || request.getFuenteIds().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona al menos dos fuentes para fusionar");
        }

        List<UUID> fuenteIds = request.getFuenteIds().stream().map(UUID::fromString).toList();
        List<FuenteDato> fuentes = fuenteDatoRepository.findAllById(fuenteIds);
        boolean todasDelProyecto = fuentes.stream().allMatch(f -> f.getProyectoId().equals(proyecto.getId()));
        if (fuentes.size() != fuenteIds.size() || !todasDelProyecto) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alguna fuente no pertenece a este proyecto");
        }

        List<Dataset> datasets = fuenteIds.stream()
            .flatMap(id -> datasetRepository.findByFuenteId(id).stream())
            .toList();
        long filasEntrada = datasets.stream().mapToLong(Dataset::getFilasDetectadas).sum();

        // Metadata de la ejecución. El merge real de filas se hace en el
        // motor de datos (pendiente); aquí se deja el resultado en estado
        // PENDIENTE para no reportar como completado algo que no se procesó.
        EjecucionProcesamiento ejecucion = new EjecucionProcesamiento(proyecto.getId(), usuario.getId(), "FUSION");
        ejecucion.setFilasEntrada(filasEntrada);
        ejecucion = ejecucionRepository.save(ejecucion);

        int siguienteVersion = datasetProcesadoRepository.findByProyectoIdOrderByVersionDesc(proyecto.getId())
            .stream().findFirst().map(dp -> dp.getVersion() + 1).orElse(1);

        return new FusionDto.EjecucionResponse(
            ejecucion.getId().toString(),
            ejecucion.getEstado(),
            ejecucion.getFilasEntrada(),
            ejecucion.getFilasSalida(),
            null,
            ejecucion.getCreatedAt()
        );
    }

    @GetMapping("/ejecuciones")
    public List<FusionDto.EjecucionResponse> listarEjecuciones(@PathVariable UUID proyectoId, Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);
        return ejecucionRepository.findByProyectoIdOrderByCreatedAtDesc(proyecto.getId()).stream()
            .filter(e -> "FUSION".equals(e.getTipoEjecucion()))
            .map(e -> new FusionDto.EjecucionResponse(
                e.getId().toString(), e.getEstado(), e.getFilasEntrada(), e.getFilasSalida(), null, e.getCreatedAt()))
            .toList();
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

package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.AnalyticsDto;
import com.crmdexter.backend.model.Dataset;
import com.crmdexter.backend.model.EsquemaCanonico;
import com.crmdexter.backend.model.Proyecto;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.DatasetProcesadoRepository;
import com.crmdexter.backend.repository.DatasetRepository;
import com.crmdexter.backend.repository.EjecucionProcesamientoRepository;
import com.crmdexter.backend.repository.EsquemaCampoRepository;
import com.crmdexter.backend.repository.EsquemaCanonicoRepository;
import com.crmdexter.backend.repository.ProyectoRepository;
import com.crmdexter.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Panel Analítico: hoy expone metadata real desde MySQL (conteos de datasets,
// filas cargadas, ejecuciones, esquema activo). Los KPIs de negocio (ingresos,
// top vendedores, series por región) no viven aquí: están dentro del contenido
// de los archivos Parquet/CSV y requieren un lector de datos (DuckDB) que
// todavía no está conectado al backend.
@RestController
@RequestMapping("/api/proyectos/{proyectoId}/analytics")
public class AnalyticsController {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DatasetRepository datasetRepository;
    private final EjecucionProcesamientoRepository ejecucionRepository;
    private final DatasetProcesadoRepository datasetProcesadoRepository;
    private final EsquemaCanonicoRepository esquemaCanonicoRepository;
    private final EsquemaCampoRepository esquemaCampoRepository;

    public AnalyticsController(ProyectoRepository proyectoRepository, UsuarioRepository usuarioRepository,
                                DatasetRepository datasetRepository,
                                EjecucionProcesamientoRepository ejecucionRepository,
                                DatasetProcesadoRepository datasetProcesadoRepository,
                                EsquemaCanonicoRepository esquemaCanonicoRepository,
                                EsquemaCampoRepository esquemaCampoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.datasetRepository = datasetRepository;
        this.ejecucionRepository = ejecucionRepository;
        this.datasetProcesadoRepository = datasetProcesadoRepository;
        this.esquemaCanonicoRepository = esquemaCanonicoRepository;
        this.esquemaCampoRepository = esquemaCampoRepository;
    }

    @GetMapping("/resumen")
    public AnalyticsDto.ResumenResponse resumen(@PathVariable UUID proyectoId, Authentication authentication) {
        Proyecto proyecto = proyectoDelUsuario(proyectoId, authentication);

        List<Dataset> datasets = datasetRepository.findByProyectoIdOrderByFechaCargaDesc(proyecto.getId());
        long totalFilas = datasets.stream().mapToLong(Dataset::getFilasDetectadas).sum();
        long totalEjecuciones = ejecucionRepository.findByProyectoIdOrderByCreatedAtDesc(proyecto.getId()).size();
        long totalProcesados = datasetProcesadoRepository.findByProyectoIdOrderByVersionDesc(proyecto.getId()).size();

        LocalDateTime ultimaEjecucion = ejecucionRepository.findByProyectoIdOrderByCreatedAtDesc(proyecto.getId())
            .stream().findFirst().map(e -> e.getCreatedAt()).orElse(null);

        Optional<EsquemaCanonico> esquemaActivo = esquemaCanonicoRepository
            .findFirstByProyectoIdAndActivoTrueOrderByVersionDesc(proyecto.getId());
        long totalCampos = esquemaActivo
            .map(e -> (long) esquemaCampoRepository.findByEsquemaIdOrderByPosicion(e.getId()).size())
            .orElse(0L);

        return new AnalyticsDto.ResumenResponse(
            datasets.size(),
            totalFilas,
            totalEjecuciones,
            totalProcesados,
            ultimaEjecucion,
            esquemaActivo.map(EsquemaCanonico::getNombre).orElse(null),
            totalCampos
        );
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

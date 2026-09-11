package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.ProyectoDto;
import com.crmdexter.backend.model.Proyecto;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.ProyectoRepository;
import com.crmdexter.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProyectoController(ProyectoRepository proyectoRepository, UsuarioRepository usuarioRepository) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<ProyectoDto.Response> listar(Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        return proyectoRepository.findByCreatedByOrderByCreatedAtDesc(usuario.getId())
            .stream().map(ProyectoDto.Response::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProyectoDto.Response crear(@Valid @RequestBody ProyectoDto.CrearRequest request,
                                      Authentication authentication) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del proyecto es obligatorio");
        }
        Usuario usuario = usuarioActual(authentication);
        String rubro = request.getRubro() == null || request.getRubro().isBlank() ? "otros" : request.getRubro().toLowerCase();
        Proyecto proyecto = new Proyecto(usuario.getOrganizacionId(), usuario.getId(), request.getNombre().trim(),
            request.getDescripcion() == null ? null : request.getDescripcion().trim(), rubro);
        return ProyectoDto.Response.from(proyectoRepository.save(proyecto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id, Authentication authentication) {
        Usuario usuario = usuarioActual(authentication);
        Proyecto proyecto = proyectoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
        if (!proyecto.getCreatedBy().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar este proyecto");
        }
        proyectoRepository.delete(proyecto);
    }

    private Usuario usuarioActual(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}

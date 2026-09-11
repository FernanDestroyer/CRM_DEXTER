package com.crmdexter.backend.service;

import com.crmdexter.backend.model.SolicitudAcceso;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.SolicitudAccesoRepository;
import com.crmdexter.backend.repository.UsuarioRepository;
import com.crmdexter.backend.service.Auth.OtpService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccessRequestService {
    private final SolicitudAccesoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public AccessRequestService(SolicitudAccesoRepository solicitudRepository, UsuarioRepository usuarioRepository,
                                OtpService otpService, PasswordEncoder passwordEncoder) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<SolicitudAcceso> pending(Authentication authentication) {
        requireAdmin(authentication);
        return solicitudRepository.findByEstadoOrderByCreatedAtAsc("PENDIENTE");
    }

    @Transactional
    public SolicitudAcceso approve(UUID id, String requestedRole, Authentication authentication) {
        Usuario admin = requireAdmin(authentication);
        SolicitudAcceso solicitud = findRequest(id);
        if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue procesada");
        }
        if (usuarioRepository.findByEmail(solicitud.getEmailSolicitante())
            .filter(usuario -> "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol())
                && "ACTIVO".equalsIgnoreCase(usuario.getEstado())).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede modificar una cuenta administradora");
        }

        String role = normalizeRole(requestedRole);
        Usuario usuario = usuarioRepository.findByEmail(solicitud.getEmailSolicitante()).orElseGet(Usuario::new);
        usuario.setOrganizacionId(admin.getOrganizacionId());
        usuario.setNombre(solicitud.getEmailSolicitante());
        usuario.setEmail(solicitud.getEmailSolicitante());
        usuario.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setRol(role);
        usuario.setEstado("ACTIVO");
        usuarioRepository.save(usuario);

        solicitud.setEstado("APROBADA");
        solicitud.setAprobadoPor(admin.getId());
        solicitud.setDecididoEn(LocalDateTime.now());
        otpService.assignAndSend(solicitud);
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public SolicitudAcceso reject(UUID id, Authentication authentication) {
        Usuario admin = requireAdmin(authentication);
        SolicitudAcceso solicitud = findRequest(id);
        if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya fue procesada");
        }
        solicitud.setEstado("RECHAZADA");
        solicitud.setAprobadoPor(admin.getId());
        solicitud.setDecididoEn(LocalDateTime.now());
        return solicitudRepository.save(solicitud);
    }

    private SolicitudAcceso findRequest(UUID id) {
        return solicitudRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
    }

    private String normalizeRole(String requestedRole) {
        String role = requestedRole == null ? "ANALISTA" : requestedRole.toUpperCase(Locale.ROOT);
        if (!List.of("PROPIETARIO", "ANALISTA", "OPERATIVO").contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no permitido");
        }
        return role;
    }

    private Usuario requireAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no autenticada");
        }
        Usuario admin = usuarioRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        if (!"ADMINISTRADOR".equalsIgnoreCase(admin.getRol()) || !"ACTIVO".equalsIgnoreCase(admin.getEstado())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requiere rol administrador");
        }
        return admin;
    }
}

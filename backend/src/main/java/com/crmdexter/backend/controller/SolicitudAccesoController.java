package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.AuthDto;
import com.crmdexter.backend.model.SolicitudAcceso;
import com.crmdexter.backend.service.AccessRequestService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/solicitudes")
public class SolicitudAccesoController {
    private final AccessRequestService accessRequestService;

    public SolicitudAccesoController(AccessRequestService accessRequestService) {
        this.accessRequestService = accessRequestService;
    }

    @GetMapping
    public List<SolicitudResponse> pending(Authentication authentication) {
        return accessRequestService.pending(authentication).stream().map(SolicitudResponse::from).toList();
    }

    @PostMapping("/{id}/aprobar")
    public SolicitudResponse approve(@PathVariable UUID id, @RequestBody(required = false) AuthDto.ApprovalRequest request,
                                     Authentication authentication) {
        String role = request == null ? null : request.getRol();
        return SolicitudResponse.from(accessRequestService.approve(id, role, authentication));
    }

    @PostMapping("/{id}/rechazar")
    public SolicitudResponse reject(@PathVariable UUID id, Authentication authentication) {
        return SolicitudResponse.from(accessRequestService.reject(id, authentication));
    }

    public record SolicitudResponse(UUID id, String email, String estado, LocalDateTime creadoEn) {
        static SolicitudResponse from(SolicitudAcceso solicitud) {
            return new SolicitudResponse(solicitud.getId(), solicitud.getEmailSolicitante(), solicitud.getEstado(), solicitud.getCreatedAt());
        }
    }
}

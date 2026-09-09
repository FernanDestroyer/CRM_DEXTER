package com.crmdexter.backend.service.Auth;

import com.crmdexter.backend.dto.AuthDto;
import com.crmdexter.backend.dto.AuthDto.LoginResponse;
import com.crmdexter.backend.model.SolicitudAcceso;
import com.crmdexter.backend.repository.SolicitudAccesoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {
    private final SolicitudAccesoRepository solicitudAccesoRepository;
    private final JwtService jwtService;

    public LoginService(SolicitudAccesoRepository solicitudAccesoRepository, JwtService jwtService) {
        this.solicitudAccesoRepository = solicitudAccesoRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDto.LoginResponse login(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        int esAdmin = solicitudAccesoRepository.isAdmin(email);

        if(esAdmin > 0) {
            String token = jwtService.generateToken(email);
            LoginResponse data = new LoginResponse("Bienvenido administrador", "administrador", token);
            return data;
        }

        if (solicitudAccesoRepository.existsByEmailSolicitanteAndEstado(
                email, "PENDIENTE")) {
            LoginResponse data = new  LoginResponse("Tiene que esperar la aprobacion del admin.", "", "");
            return data;
        }

        SolicitudAcceso solicitud = new SolicitudAcceso();
        solicitud.setEmailSolicitante(email);
        solicitud.setEstado("PENDIENTE");
        solicitud.setNotificacionLeida(false);
        solicitud.setIntentosOtp(0);

        solicitudAccesoRepository.save(solicitud);

        return new AuthDto.LoginResponse("Solicitud enviada. Espera la aprobación del administrador","","");
    }
}

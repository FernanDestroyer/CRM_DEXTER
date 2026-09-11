package com.crmdexter.backend.service.Auth;

import com.crmdexter.backend.dto.AuthDto;
import com.crmdexter.backend.model.SolicitudAcceso;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.SolicitudAccesoRepository;
import com.crmdexter.backend.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {
    private final SolicitudAccesoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public LoginService(SolicitudAccesoRepository solicitudRepository, UsuarioRepository usuarioRepository,
                        JwtService jwtService, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @Transactional
    public AuthDto.LoginResponse login(String rawEmail) {
        String email = normalizeEmail(rawEmail);
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && isActiveAdmin(usuario)) {
            return authenticated(usuario, "Bienvenido administrador");
        }

        SolicitudAcceso solicitud = solicitudRepository
            .findTopByEmailSolicitanteIgnoreCaseOrderByCreatedAtDesc(email).orElse(null);
        if (solicitud != null && "APROBADA".equals(solicitud.getEstado())) {
            return new AuthDto.LoginResponse("Ingresa el código OTP enviado a tu correo.",
                usuario == null ? "" : usuario.getRol().toLowerCase(Locale.ROOT), "", "OTP_REQUIRED");
        }
        if (usuario != null && "ACTIVO".equalsIgnoreCase(usuario.getEstado()) && solicitud != null
            && "OTP_VERIFICADO".equals(solicitud.getEstado())) {
            solicitud.setEstado("APROBADA");
            otpService.assignAndSend(solicitud);
            solicitudRepository.save(solicitud);
            return new AuthDto.LoginResponse("Ingresa el código OTP enviado a tu correo.",
                usuario.getRol().toLowerCase(Locale.ROOT), "", "OTP_REQUIRED");
        }
        if (solicitud != null && "PENDIENTE".equals(solicitud.getEstado())) {
            return new AuthDto.LoginResponse("Solicitud enviada. Espera la aprobación del administrador.", "", "", "PENDING");
        }
        if (usuario != null && !"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            return new AuthDto.LoginResponse("Tu cuenta no está activa. Contacta al administrador.", "", "", "REJECTED");
        }

        SolicitudAcceso nueva = new SolicitudAcceso();
        nueva.setEmailSolicitante(email);
        nueva.setEstado("PENDIENTE");
        nueva.setNotificacionLeida(false);
        nueva.setIntentosOtp(0);
        solicitudRepository.save(nueva);
        return new AuthDto.LoginResponse("Solicitud enviada. Espera la aprobación del administrador.", "", "", "PENDING");
    }

    @Transactional
    public AuthDto.LoginResponse verifyOtp(String rawEmail, String otp) {
        String email = normalizeEmail(rawEmail);
        if (otp == null || !otp.matches("\\d{4}")) {
            throw new IllegalArgumentException("El OTP debe tener exactamente 4 dígitos");
        }
        SolicitudAcceso solicitud = solicitudRepository.findTopByEmailSolicitanteIgnoreCaseOrderByCreatedAtDesc(email)
            .orElseThrow(() -> new IllegalArgumentException("No existe una solicitud para este correo"));
        if (!"APROBADA".equals(solicitud.getEstado()) || solicitud.getOtpExpiraEn() == null
            || solicitud.getOtpExpiraEn().isBefore(LocalDateTime.now())) {
            solicitud.setEstado("EXPIRADA");
            throw new IllegalArgumentException("El OTP no es válido o ha expirado");
        }
        if (solicitud.getIntentosOtp() >= 5) {
            throw new IllegalArgumentException("Se excedió el número máximo de intentos");
        }
        if (!passwordEncoder.matches(otp, solicitud.getOtpHash())) {
            solicitud.setIntentosOtp(solicitud.getIntentosOtp() + 1);
            throw new IllegalArgumentException("El OTP ingresado es incorrecto");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("La cuenta aprobada no existe"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        solicitud.setEstado("OTP_VERIFICADO");
        solicitud.setOtpHash(null);
        solicitud.setOtpExpiraEn(null);
        usuarioRepository.save(usuario);
        return authenticated(usuario, "Acceso concedido");
    }

    private AuthDto.LoginResponse authenticated(Usuario usuario, String message) {
        return new AuthDto.LoginResponse(message, usuario.getRol().toLowerCase(Locale.ROOT),
            jwtService.generateToken(usuario.getEmail()), "AUTHENTICATED");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank() || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El email no es válido");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isActiveAdmin(Usuario usuario) {
        return "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol()) && "ACTIVO".equalsIgnoreCase(usuario.getEstado());
    }
}

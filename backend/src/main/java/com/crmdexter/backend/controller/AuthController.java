package com.crmdexter.backend.controller;

import com.crmdexter.backend.dto.AuthDto;
import com.crmdexter.backend.model.Usuario;
import com.crmdexter.backend.repository.UsuarioRepository;
import com.crmdexter.backend.service.Auth.LoginService;
import java.time.Duration;
import java.util.Locale;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String COOKIE_NAME = "crm_dexter_token";
    private final LoginService loginService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(LoginService loginService, UsuarioRepository usuarioRepository) {
        this.loginService = loginService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return withSessionCookie(loginService.login(request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthDto.LoginResponse> verifyOtp(@RequestBody AuthDto.OtpRequest request) {
        return withSessionCookie(loginService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no autenticada");
        }
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return UsuarioResponse.from(usuario);
    }

    @GetMapping("/session")
    public SessionResponse session(Authentication authentication) {
        boolean authenticated = authentication != null && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
        return new SessionResponse(authenticated);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
            .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(Duration.ZERO).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    private ResponseEntity<AuthDto.LoginResponse> withSessionCookie(AuthDto.LoginResponse response) {
        if ("AUTHENTICATED".equals(response.getStatus()) && response.getToken() != null) {
            ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, response.getToken())
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(Duration.ofHours(24)).build();
            response.setToken(null);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        }
        return ResponseEntity.ok(response);
    }

    public record UsuarioResponse(String id, String email, String nombre, String rol, String estado) {
        static UsuarioResponse from(Usuario usuario) {
            return new UsuarioResponse(usuario.getId().toString(), usuario.getEmail(), usuario.getNombre(),
                usuario.getRol().toLowerCase(Locale.ROOT), usuario.getEstado().toLowerCase(Locale.ROOT));
        }
    }

    public record SessionResponse(boolean authenticated) { }
}

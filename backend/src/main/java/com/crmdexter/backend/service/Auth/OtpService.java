package com.crmdexter.backend.service.Auth;

import com.crmdexter.backend.model.SolicitudAcceso;
import com.crmdexter.backend.service.Email.EmailJsEmailService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private final SecureRandom random = new SecureRandom();
    private final PasswordEncoder passwordEncoder;
    private final EmailJsEmailService emailService;
    private final int expirationSeconds;

    public OtpService(PasswordEncoder passwordEncoder, EmailJsEmailService emailService,
                      @Value("${app.otp.expiration-seconds:300}") int expirationSeconds) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.expirationSeconds = expirationSeconds;
    }

    public void assignAndSend(SolicitudAcceso solicitud) {
        String otp = String.format("%04d", random.nextInt(10000));
        solicitud.setOtpHash(passwordEncoder.encode(otp));
        solicitud.setOtpExpiraEn(LocalDateTime.now().plusSeconds(expirationSeconds));
        solicitud.setIntentosOtp(0);
        emailService.sendOtp(solicitud.getEmailSolicitante(), otp, expirationSeconds / 60);
    }
}

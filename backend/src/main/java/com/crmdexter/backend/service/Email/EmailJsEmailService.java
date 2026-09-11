package com.crmdexter.backend.service.Email;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class EmailJsEmailService {
    private final RestClient client = RestClient.create("https://api.emailjs.com");
    private final String serviceId;
    private final String templateId;
    private final String publicKey;
    private final String privateKey;

    public EmailJsEmailService(@Value("${app.emailjs.service-id:}") String serviceId,
                               @Value("${app.emailjs.template-id:}") String templateId,
                               @Value("${app.emailjs.public-key:}") String publicKey,
                               @Value("${app.emailjs.private-key:}") String privateKey) {
        this.serviceId = serviceId;
        this.templateId = templateId;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public void sendOtp(String recipient, String otp, int expirationMinutes) {
        if (serviceId.isBlank() || templateId.isBlank() || publicKey.isBlank() || privateKey.isBlank()) {
            throw new EmailDeliveryException("Falta configurar EMAILJS_SERVICE_ID, EMAILJS_TEMPLATE_ID, EMAILJS_PUBLIC_KEY o EMAILJS_PRIVATE_KEY");
        }
        try {
            client.post()
                .uri("/api/v1.0/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                    "service_id", serviceId,
                    "template_id", templateId,
                    "user_id", publicKey,
                    "accessToken", privateKey,
                    "template_params", Map.of(
                        "to_email", recipient,
                        "otp", otp,
                        "expiration_minutes", expirationMinutes
                    )
                ))
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new EmailDeliveryException("EmailJS rechazó el correo: " + exception.getResponseBodyAsString(), exception);
        } catch (RestClientException exception) {
            throw new EmailDeliveryException("No se pudo conectar con EmailJS", exception);
        }
    }
}

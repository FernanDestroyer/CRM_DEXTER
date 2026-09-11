package com.crmdexter.backend.config;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.crmdexter.backend.dto.ErrorResponseDto;

import jakarta.security.auth.message.AuthException;
import org.springframework.web.server.ResponseStatusException;
import com.crmdexter.backend.service.Email.EmailDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestControllerAdvice 
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
 
    
    // para errores de autenticacion HTTP 401
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthException(AuthException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(),ex.getMessage(),java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class, ResponseStatusException.class})
    public ResponseEntity<ErrorResponseDto> handleClientException(Exception ex) {
        int status = ex instanceof ResponseStatusException response
            ? response.getStatusCode().value() : HttpStatus.BAD_REQUEST.value();
        ErrorResponseDto errorResponse = new ErrorResponseDto(status, ex.getMessage(), java.time.LocalDateTime.now());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailDeliveryException(Exception ex) {
        logger.error("Error enviando OTP por correo", ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(),
            "No se pudo enviar el código OTP. Verifica la configuración del servicio de correo.", java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // para errores no controlados HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}

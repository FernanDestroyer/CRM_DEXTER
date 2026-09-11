package com.crmdexter.backend.dto;

public class AuthDto {
    

    // dto para loguearse
    public static class LoginRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email;}
    }

    public static class OtpRequest {
        private String email;
        private String otp;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class ApprovalRequest {
        private String rol;
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }

    // dto para responder a la solicitud de acceso
    public static class LoginResponse {
        private String message;
        private String rol;
        private String token;
        private String status;


        public LoginResponse(String message, String rol, String token) { this(message, rol, token, token == null || token.isBlank() ? "PENDING" : "AUTHENTICATED"); }
        public LoginResponse(String message, String rol, String token, String status) { this.message = message; this.rol = rol; this.token = token; this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
        public String getToken() {return token;}
        public void setToken(String token) {this.token = token;}
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    




}

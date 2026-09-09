package com.crmdexter.backend.dto;

public class AuthDto {
    

    // dto para loguearse
    public static class LoginRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email;}
    }

    // dto para responder a la solicitud de acceso
    public static class LoginResponse {
        private String message;
        private String rol;
        private String token;


        public LoginResponse(String message, String rol, String token) { this.message = message; this.rol = rol; this.token = token; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
        public String getToken() {return token;}
        public void setToken(String token) {this.token = token;}
    }
    




}

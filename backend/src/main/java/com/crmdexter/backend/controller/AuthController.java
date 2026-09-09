package com.crmdexter.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.crmdexter.backend.dto.AuthDto;
import com.crmdexter.backend.dto.AuthDto.LoginResponse;
import com.crmdexter.backend.service.Auth.LoginService;


@RestController 
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    // Aquí puedes agregar los endpoints de autenticación, como login, registro, etc.

    @PostMapping("/login")
    public AuthDto.LoginResponse login(@RequestBody AuthDto.LoginRequest pepe) {
        
        LoginResponse data = loginService.login(pepe.getEmail());
        return data;
    }
    


}

package com.sep490.gshop.controller;

import com.sep490.gshop.common.URLConstant;
import com.sep490.gshop.payload.request.LoginRequest;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URLConstant.AUTH)
@Log4j2
@CrossOrigin("*")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public AuthUserResponse login(@Valid LoginRequest loginRequest) {
        log.info("login() AuthController start | email: {}", loginRequest.getEmail());
        AuthUserResponse jwt = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        log.info("login() AuthController end | jwt: {}", jwt);
        return jwt;
    }

    @PostMapping("/test")
    public String test() {
        return "Test successful";
    }

    @PostMapping("register")
    public AuthUserResponse register(@Valid RegisterRequest registerRequest) {
        log.info("register() AuthController start | email: {}", registerRequest.getEmail());
        AuthUserResponse response = authService.register(registerRequest);
        log.info("register() AuthController end | response: {}", response);
        return response;
    }

}

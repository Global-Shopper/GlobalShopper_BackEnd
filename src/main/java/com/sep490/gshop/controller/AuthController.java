package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.config.handler.ErrorMessage;
import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.payload.request.ForgotPasswordRequest;
import com.sep490.gshop.payload.request.LoginRequest;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public AuthUserResponse login(@Valid @RequestBody LoginRequest loginRequest) {
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
    public RedirectMessage register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("register() AuthController start | email: {}", registerRequest.getEmail());
        RedirectMessage response = authService.register(registerRequest);
        log.info("register() AuthController end | response: {}", response);
        return response;
    }

    @PostMapping("/verify-otp")
    public AuthUserResponse verifyOtp(String email, String otp) {
        log.info("verifyOtp() AuthController start | email: {}", email);
        AuthUserResponse response = authService.verifyOtp(email, otp);
        log.info("verifyOtp() AuthController end | response: {}", response);
        return response;
    }

    @GetMapping("/resend-otp")
    public ErrorMessage resendOtp(String email) {
        log.info("resendOtp() AuthController start | email: {}", email);
        ErrorMessage response = authService.resendOtp(email);
        log.info("resendOtp() AuthController end | response: {}", response);
        return response;
    }

    @GetMapping("/forgot-password")
    public RedirectMessage forgotPassword(@RequestParam String email) {
        log.info("forgotPassword() AuthController start | email: {}", email);
        RedirectMessage response = authService.forgotPassword(email);
        log.info("forgotPassword() AuthController end | email: {}", response);
        return response;
    }
    @PutMapping("/forgot-password/reset")
    public AuthUserResponse forgotPasswordReset(@RequestBody ForgotPasswordRequest forgotPasswordRequest,@RequestParam String otp) {
        log.info("forgotPasswordReset() AuthController start | email: {}", forgotPasswordRequest.getEmail());
        var response = authService.resetPassword(forgotPasswordRequest, otp);
        log.info("forgotPasswordReset() AuthController end | response: {}", response);
        return response;
    }
}

package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.config.handler.ErrorMessage;
import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.payload.request.LoginRequest;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.request.ResetPasswordRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.payload.response.ResetPasswordValidResponse;
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

    @GetMapping("/resend-otp")
    public ErrorMessage resendOtp(String email) {
        log.info("resendOtp() AuthController start | email: {}", email);
        ErrorMessage response = authService.resendOtp(email);
        log.info("resendOtp() AuthController end | response: {}", response);
        return response;
    }
    @GetMapping("/forgot-password")
    public ErrorMessage requestForgotPassword(@RequestParam String email) {
        log.info("requestForgotPassword() start | email: {}", email);
        ErrorMessage response = authService.forgotPassword(email);
        log.info("requestForgotPassword() end | response: {}", response);
        return response;
    }

    @GetMapping("/forgot-password/verify")
    public ResetPasswordValidResponse verifyForgotPasswordOtp(@RequestParam String otp, @RequestParam String email) {
        log.info("verifyForgotPasswordOtp() start | email: {}", email);
        ResetPasswordValidResponse response = authService.verifyOtpResetPassword(otp, email);
        log.info("verifyForgotPasswordOtp() end | response: {}", response);
        return response;
    }

    @PutMapping("/forgot-password/reset")
    public AuthUserResponse resetForgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("resetForgotPassword() start");
        AuthUserResponse response = authService.resetPassword(resetPasswordRequest.getPassword(), resetPasswordRequest.getToken());
        log.info("resetForgotPassword() end | response: {}", response);
        return response;
    }

}

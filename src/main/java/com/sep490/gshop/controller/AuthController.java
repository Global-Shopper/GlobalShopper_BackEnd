package com.sep490.gshop.controller;

import com.sep490.gshop.common.constants.URLConstant;
import com.sep490.gshop.payload.request.ChangePasswordRequest;
import com.sep490.gshop.payload.request.LoginRequest;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.request.ResetPasswordRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.ResetPasswordValidResponse;
import com.sep490.gshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Đăng nhập tài khoản")
    public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("login() AuthController start | email: {}", loginRequest.getEmail());
        AuthUserResponse jwt = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        log.info("login() AuthController end | jwt: {}", jwt);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("register() AuthController start | email: {}", registerRequest.getEmail());
        MessageResponse response = authService.register(registerRequest);
        log.info("register() AuthController end | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Xác thực OTP đăng nhập hoặc đăng ký")
    public ResponseEntity<AuthUserResponse> verifyOtp(String email, String otp) {
        log.info("verifyOtp() AuthController start | email: {}", email);
        AuthUserResponse response = authService.verifyOtp(email, otp);
        log.info("verifyOtp() AuthController end | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resend-otp")
    @Operation(summary = "Gửi lại mã OTP xác thực cho email")
    public ResponseEntity<MessageResponse> resendOtp(String email) {
        log.info("resendOtp() AuthController start | email: {}", email);
        MessageResponse response = authService.resendOtp(email);
        log.info("resendOtp() AuthController end | response: {}", response);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/forgot-password")
    @Operation(summary = "Nhập email để gửi otp cho quên mật khẩu")
    public ResponseEntity<MessageResponse> requestForgotPassword(@RequestParam String email) {
        log.info("requestForgotPassword() start | email: {}", email);
        MessageResponse response = authService.forgotPassword(email);
        log.info("requestForgotPassword() end | response: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password/verify")
    @Operation(summary = "Xác thực OTP khi quên mật khẩu")
    public ResetPasswordValidResponse verifyForgotPasswordOtp(@RequestParam String otp, @RequestParam String email) {
        log.info("verifyForgotPasswordOtp() start | email: {}", email);
        ResetPasswordValidResponse response = authService.verifyOtpResetPassword(otp, email);
        log.info("verifyForgotPasswordOtp() end | response: {}", response);
        return response;
    }
    @PutMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("changePassword() AuthController start");
        MessageResponse newMessage = authService.changePassword(changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        log.info("changePassword() AuthController end");
        return ResponseEntity.ok(newMessage);
    }
    @PutMapping("/forgot-password/reset")
    @Operation(summary = "Đổi mật khẩu (reset)")
    public AuthUserResponse resetForgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("resetForgotPassword() start");
        AuthUserResponse response = authService.resetPassword(resetPasswordRequest.getPassword(), resetPasswordRequest.getToken());
        log.info("resetForgotPassword() end | response: {}", response);
        return response;
    }

    @Operation(summary = "Thay đổi email của user hiện tại")
    @PostMapping("/change-email")
    public MessageResponse changeEmail() {
        log.info("changeEmail() Start");
        MessageResponse response = authService.changeMail();
        log.info("changeEmail() End | response: {}", response);
        return response;
    }

    @Operation(summary = "Nhập mail mới và xác thực bằng OTP")
    @PostMapping("/verify-otp-and-change-email")
    public MessageResponse verifyEmail(
            @RequestParam String newEmail,
            @RequestParam String otp) {
        log.info("verifyEmail() Start | newEmail: {}, otp: {}", newEmail, otp);
        MessageResponse response = authService.verifyMail(otp, newEmail);
        log.info("verifyEmail() End | response: {}", response);
        return response;
    }

    @GetMapping("/verify-new-email")
    @Operation(summary = "Xác thực mail mới")
    public ResponseEntity<Void> verifyNewEmail(@RequestParam("token") String token) {
        log.info("verifyNewEmail() Start | token: {}", token);
        var response =  authService.verifyToUpdateEmail(token);
        log.info("verifyNewEmail() End | response: {}", response);
        return ResponseEntity.ok().build();

    }


}

package com.sep490.gshop.service;

import com.sep490.gshop.config.handler.ErrorMessage;
import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.payload.response.ResetPasswordValidResponse;

public interface AuthService {
    AuthUserResponse login(String email, String password);

    RedirectMessage register(RegisterRequest registerRequest);

    ErrorMessage resendOtp(String email);
    ErrorMessage forgotPassword(String email);

    ResetPasswordValidResponse verifyOtpResetPassword(String otp, String email);

    AuthUserResponse resetPassword(String newPassword, String token);

}

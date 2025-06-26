package com.sep490.gshop.service;

import com.sep490.gshop.config.handler.ErrorMessage;
import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.payload.request.ForgotPasswordRequest;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;

public interface AuthService {
    AuthUserResponse login(String email, String password);

    RedirectMessage register(RegisterRequest registerRequest);

    AuthUserResponse verifyOtp(String email, String otp);

    ErrorMessage resendOtp(String email);
    RedirectMessage resendOtpForgotPassword(String email);

    RedirectMessage forgotPassword(String email);

    AuthUserResponse resetPassword(ForgotPasswordRequest request, String otp);

}

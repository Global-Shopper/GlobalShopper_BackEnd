package com.sep490.gshop.service;

import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;

public interface AuthService {
    AuthUserResponse login(String email, String password);

    RedirectMessage register(RegisterRequest registerRequest);

    AuthUserResponse verifyOtp(String email, String otp);

    RedirectMessage resendOtp(String email);
}

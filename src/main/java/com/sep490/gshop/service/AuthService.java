package com.sep490.gshop.service;

import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;

public interface AuthService {
    AuthUserResponse login(String email, String password);

    AuthUserResponse register(RegisterRequest registerRequest);
}

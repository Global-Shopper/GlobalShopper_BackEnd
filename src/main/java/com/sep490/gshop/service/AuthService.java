package com.sep490.gshop.service;

import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.ResetPasswordValidResponse;

public interface AuthService {
    AuthUserResponse login(String email, String password);
    MessageResponse register(RegisterRequest registerRequest);
    MessageResponse resendOtp(String email);
    MessageResponse forgotPassword(String email);
    MessageResponse changePassword(String oldPassword, String newPassword);
    ResetPasswordValidResponse verifyOtpResetPassword(String otp, String email);
    AuthUserResponse verifyOtp(String email, String otp);
    AuthUserResponse resetPassword(String newPassword, String token);
    //Update mail mới
    MessageResponse changeMail();
    AuthUserResponse verifyMail(String otp, String email);

    MessageResponse confirmNewMail(String otp);

}

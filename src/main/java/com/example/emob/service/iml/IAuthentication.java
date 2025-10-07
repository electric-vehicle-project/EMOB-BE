package com.example.emob.service.iml;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.OtpRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.request.TokenRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.model.response.OtpResponse;

public interface IAuthentication {
    APIResponse<AccountResponse> login (LoginRequest request);

    APIResponse<AccountResponse> register (RegisterRequest request);
    APIResponse<AccountResponse> refresh(TokenRequest refreshRequest);
    void logout(TokenRequest refreshRequest);

    void forgotPassword (OtpRequest request);

    APIResponse<OtpResponse> verifyOtp (OtpRequest request, String otp);

    void resetPassword (String token, String newPassword);
}

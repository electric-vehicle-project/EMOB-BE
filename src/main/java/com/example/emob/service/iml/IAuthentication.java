package com.example.emob.service.iml;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.request.TokenRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;

public interface IAuthentication {
    APIResponse<AccountResponse> login (LoginRequest request);

    APIResponse<AccountResponse> register (RegisterRequest request);
    public APIResponse<AccountResponse> refresh(TokenRequest refreshRequest);
    void logout(TokenRequest refreshRequest);
}

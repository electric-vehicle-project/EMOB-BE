/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.OtpRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.request.TokenRequest;
import com.example.emob.model.response.*;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IAuthentication {
  APIResponse<AccountResponse> login(LoginRequest request);

  APIResponse<AccountResponse> registerByAdmin(RegisterRequest request);

  APIResponse<AccountResponse> registerByManager(RegisterRequest request);

  APIResponse<AccountResponse> refresh(TokenRequest refreshRequest);

  APIResponse<AccountResponse> get(UUID id);

  APIResponse<PageResponse<AccountResponse>> getAllByAdmin(Pageable pageable);

  APIResponse<PageResponse<AccountResponse>> getAllByManager(Pageable pageable);

  void logout(TokenRequest refreshRequest);

  void forgotPassword(OtpRequest request);

  APIResponse<OtpResponse> verifyOtp(OtpVerifyRequest request);

  APIResponse<Void> resetPassword(String newPassword);

  void resendOtp();
}

/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.constant.Role;
import com.example.emob.model.request.*;
import com.example.emob.model.response.*;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IAuthentication {
  APIResponse<AccountResponse> login(LoginRequest request);

  APIResponse<AccountResponse> registerByAdmin(RegisterRequest request);

  APIResponse<AccountResponse> registerByManager(RegisterRequest request);

  APIResponse<AccountResponse> refresh(TokenRequest refreshRequest);

  APIResponse<AccountResponse> get(UUID id);

  public APIResponse<PageResponse<AccountResponse>> getAllByAdmin(List<Role> roles, Pageable pageable);

  APIResponse<PageResponse<AccountResponse>> getAllByManager(Pageable pageable);

  void logout(TokenRequest refreshRequest);

  void forgotPassword(OtpRequest request);

  APIResponse<OtpResponse> verifyOtp(OtpVerifyRequest request);

  APIResponse<Void> resetPassword(String newPassword);

  APIResponse<Void> resendOtp(String email);

  APIResponse<Void> deleteAccount(UUID id);

  APIResponse<Void> changeStatus(UUID id);
  APIResponse<AccountResponse> loginByGoogle(TokenRequest tokenRequest);
  APIResponse<AccountResponse> updateProfile(UpdateProfileRequest request);

}

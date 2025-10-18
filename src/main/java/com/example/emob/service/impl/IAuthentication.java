/* EMOB-2025 */
package com.example.emob.service.impl;

import com.example.emob.model.request.*;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.model.response.OtpResponse;

public interface IAuthentication {
  APIResponse<AccountResponse> login(LoginRequest request);

  APIResponse<AccountResponse> register(RegisterRequest request);

  APIResponse<AccountResponse> refresh(TokenRequest refreshRequest);

  void logout(TokenRequest refreshRequest);

  void forgotPassword(OtpRequest request);

  APIResponse<OtpResponse> verifyOtp(OtpVerifyRequest request);

  APIResponse<Void> resetPassword(String newPassword);

  void resendOtp();
}

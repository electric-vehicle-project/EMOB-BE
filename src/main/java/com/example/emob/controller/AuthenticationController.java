/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.model.request.*;
import com.example.emob.model.response.*;
import com.example.emob.service.AuthenticationService;
import com.fasterxml.jackson.core.io.JsonEOFException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Tag(name = "Authentication Controller", description = "Endpoints for user registration and login")
@SecurityRequirement(name = "api")
public class AuthenticationController {
  @Autowired AuthenticationService authenticationService;

  @PostMapping("/forgot-password")
  public void forgotPassword(@Valid @RequestBody OtpRequest request) {
    authenticationService.forgotPassword(request);
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<APIResponse<OtpResponse>> verifyOtp(
      @Valid @RequestBody OtpVerifyRequest request) {
    return ResponseEntity.ok(authenticationService.verifyOtp(request));
  }

  @PostMapping("/reset-password")
  public APIResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    return authenticationService.resetPassword(request.getNewPassword());
  }

  @PostMapping("/resend-otp")
  public void resendOtp() {
    authenticationService.resendOtp();
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login Account",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Login Account",
              required = true,
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = LoginRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Account Admin",
                            description = "Example login for Admin",
                            value =
                                """
                  {
                    "email": "admin@gmail.com",
                    "password": "Admin@123"
                  }
                  """),
                        @ExampleObject(
                            name = "Account A",
                            description = "Example login for Alice",
                            value =
                                """
        {
          "email": "alice@example.com",
          "password": "Pass1234"
        }
        """),
                        @ExampleObject(
                            name = "Account B",
                            description = "Example login for Bob",
                            value =
                                """
                    {
                      "email": "bob@example.com",
                      "password": "Pass5678"
                    }
                    """),
                        @ExampleObject(
                            name = "Account C",
                            description = "Example login for Alice",
                            value =
                                """
                                                                            {
                                                                              "email": "alice123@example.com",
                                                                              "password": "Pass1234"
                                                                            }
                        """),
                        @ExampleObject(
                            name = "Account D",
                            description = "Example login for Yob",
                            value =
                                """
                      {
                        "email": "yob@example.com",
                        "password": "Pass5678"
                      }
                      """)
                      })))
  public ResponseEntity<APIResponse<AccountResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authenticationService.login(request));
  }

  @PostMapping("/register-by-manager")
  @Operation(
      summary = "Register a new Account",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RegisterRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Account C",
                            value =
                                """
                    {
                      "fullName": "Alice",
                      "gender": "FEMALE",
                      "address": "123 Elm St",
                      "dateOfBirth": "1990-01-01",
                      "phone": "09876543211",
                      "email": "alice123@example.com",
                      "password": "Pass1234"
                    }
                    """),
                        @ExampleObject(
                            name = "Account D",
                            value =
                                """
                  {
                    "fullName": "Yob",
                    "gender": "MALE",
                    "address": "456 Oak St",
                    "dateOfBirth": "1992-02-02",
                    "phone": "1012345678912",
                    "email": "yob@example.com",
                    "password": "Pass5678"
                  }
                  """)
                      })))
  public ResponseEntity<APIResponse<AccountResponse>> registerByManager(
      @Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authenticationService.registerByManager(request));
  }

  @PostMapping("/register-by-admin")
  @Operation(
      summary = "Register a new Account",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = RegisterRequest.class),
                      examples = {
                        @ExampleObject(
                            name = "Account A",
                            value =
                                """
                      {
                        "fullName": "Alice",
                        "dealerId": "nhập id hoặc ko nhập",
                        "gender": "FEMALE",
                        "address": "123 Elm St",
                        "dateOfBirth": "1990-01-01",
                        "phone": "0987654321",
                        "email": "alice@example.com",
                        "password": "Pass1234"
                      }
                      """),
                        @ExampleObject(
                            name = "Account B",
                            value =
                                """
                                  {
                                    "fullName": "Bob",
                                    "dealerId": "nhập id hoặc ko nhập",
                                    "gender": "MALE",
                                    "address": "456 Oak St",
                                    "dateOfBirth": "1992-02-02",
                                    "phone": "0123456789",
                                    "email": "bob@example.com",
                                    "password": "Pass5678"
                                  }
                                  """),
                      })))
  public ResponseEntity<APIResponse<AccountResponse>> registerByAdmin(
      @Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authenticationService.registerByAdmin(request));
  }

  @PostMapping("/logout")
  @Operation(summary = "Logout a account")
  public ResponseEntity<APIResponse<Void>> logout(@RequestBody TokenRequest refreshRequest)
      throws ParseException, JsonEOFException {
    authenticationService.logout(refreshRequest);
    return ResponseEntity.ok(
        APIResponse.<Void>builder().code(200).message("Logout successfully").build());
  }

  @PostMapping("/refresh")
  @Operation(summary = "refresh token")
  public ResponseEntity<APIResponse<AccountResponse>> refresh(
      @RequestBody TokenRequest refreshRequest) {
    return ResponseEntity.ok(authenticationService.refresh(refreshRequest));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get account by ID")
  public ResponseEntity<APIResponse<AccountResponse>> getAccount(@PathVariable UUID id) {
    return ResponseEntity.ok(authenticationService.get(id));
  }

  @GetMapping("by-manager")
  @Operation(summary = "Get all by manager")
  public ResponseEntity<APIResponse<PageResponse<AccountResponse>>> getAllByManager(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(authenticationService.getAllByManager(pageable));
  }

  @GetMapping("by-admin")
  public ResponseEntity<APIResponse<PageResponse<AccountResponse>>> getAllByAdmin(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(authenticationService.getAllByAdmin(pageable));
  }
}

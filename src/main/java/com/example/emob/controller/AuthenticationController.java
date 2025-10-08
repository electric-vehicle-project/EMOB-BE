/* EMOB-2025 */
package com.example.emob.controller;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.request.TokenRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.service.AuthenticationService;
import com.fasterxml.jackson.core.io.JsonEOFException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Tag(name = "Authentication Controller", description = "Endpoints for user registration and login")
@SecurityRequirement(name = "api")
public class AuthenticationController {
    @Autowired AuthenticationService authenticationService;

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
                        """)
                                            })))
    public ResponseEntity<APIResponse<AccountResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new Account",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema =
                                                    @Schema(implementation = RegisterRequest.class),
                                            examples = {
                                                @ExampleObject(
                                                        name = "Account A",
                                                        value =
                                                                """
                {
                  "fullName": "Alice",
                  "gender": "FEMALE",
                  "status": "ACTIVE",
                  "address": "123 Elm St",
                  "dateOfBirth": "1990-01-01",
                  "role": "ADMIN",
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
                  "gender": "MALE",
                  "status": "ACTIVE",
                  "address": "456 Oak St",
                  "dateOfBirth": "1992-02-02",
                  "role": "EVM_STAFF",
                  "phone": "0123456789",
                  "email": "bob@example.com",
                  "password": "Pass5678"
                }
                """),
                                                    @ExampleObject(
                                                            name = "Account C",
                                                            value =
                                                                    """
                    {
                      "fullName": "Alice",
                      "gender": "FEMALE",
                      "status": "ACTIVE",
                      "address": "123 Elm St",
                      "dateOfBirth": "1990-01-01",
                      "role": "DEALER_STAFF",
                      "phone": "09876543211",
                      "email": "alice123@example.com",
                      "password": "Pass1234"
                    }
                    """)
                                            })))
    public ResponseEntity<APIResponse<AccountResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
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
}

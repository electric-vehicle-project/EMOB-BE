package com.example.emob.controller;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@Tag(name = "Authentication Controller", description = "Endpoints for user registration and login")
@SecurityRequirement(name = "api")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(
            summary = "Login Account",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login Account",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Account A",
                                            description = "Example login for Alice",
                                            value = """
                    {
                      "email": "alice@example.com",
                      "password": "Pass1234"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Account B",
                                            description = "Example login for Bob",
                                            value = """
                    {
                      "email": "bob@example.com",
                      "password": "Pass5678"
                    }
                    """
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<AccountResponse>> login (@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Register a new Account",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = {
                                    @ExampleObject(name = "Account A", value = """
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
                                    @ExampleObject(name = "Account B", value = """
                {
                  "fullName": "Bob",
                  "gender": "MALE",
                  "status": "ACTIVE",
                  "address": "456 Oak St",
                  "dateOfBirth": "1992-02-02",
                  "role": "STAFF",
                  "phone": "0123456789",
                  "email": "bob@example.com",
                  "password": "Pass5678"
                }
                """)
                            }
                    )
            )
    )
    public ResponseEntity<APIResponse<AccountResponse>> register (@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
}

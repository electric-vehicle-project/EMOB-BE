package com.example.emob.controller;

import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<AccountResponse>> login (@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<AccountResponse>> register (@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
}

package com.alhakim.issuetracker.controller;

import com.alhakim.issuetracker.dto.BaseResponse;
import com.alhakim.issuetracker.dto.LoginRequest;
import com.alhakim.issuetracker.dto.LoginResponse;
import com.alhakim.issuetracker.dto.RegisterRequest;
import com.alhakim.issuetracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.Register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.Login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(token));
    }
}

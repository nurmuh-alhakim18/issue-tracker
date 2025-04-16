package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.LoginRequest;
import com.alhakim.issuetracker.dto.RegisterRequest;

public interface AuthService {
    void Register(RegisterRequest registerRequest);
    String Login(LoginRequest loginRequest);
}

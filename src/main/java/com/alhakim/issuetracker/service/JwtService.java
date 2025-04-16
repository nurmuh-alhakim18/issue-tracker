package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.MyUserDetails;

public interface JwtService {
    String generateToken(MyUserDetails user);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}

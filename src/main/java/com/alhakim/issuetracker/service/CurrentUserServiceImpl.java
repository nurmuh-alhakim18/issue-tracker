package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.MyUserDetails;
import com.alhakim.issuetracker.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof MyUserDetails userDetails)) {
            throw new UnauthorizedException("User is not authenticated");
        }

        return userDetails.getUser().getId();
    }
}

package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.LoginRequest;
import com.alhakim.issuetracker.dto.LoginResponse;
import com.alhakim.issuetracker.dto.MyUserDetails;
import com.alhakim.issuetracker.dto.RegisterRequest;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.entity.UserRole;
import com.alhakim.issuetracker.entity.UserRole.UserRoleId;
import com.alhakim.issuetracker.exception.DuplicateException;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.exception.UnauthorizedException;
import com.alhakim.issuetracker.repository.RoleRepository;
import com.alhakim.issuetracker.repository.UserRepository;
import com.alhakim.issuetracker.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private static final String ROLE_USER = "ROLE_USER";

    @Override
    @Transactional
    public void Register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateException("Email already exists");
        }

        String password = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(password)
                .build();
        userRepository.save(user);

        Long roleId = roleRepository
                .findRoleIdByName(ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(user.getId(), roleId))
                .build();
        userRoleRepository.save(userRole);
    }

    @Override
    public String Login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            return jwtService.generateToken(userDetails);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }
}

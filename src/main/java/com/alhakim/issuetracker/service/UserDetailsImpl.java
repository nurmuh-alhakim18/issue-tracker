package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.dto.MyUserDetails;
import com.alhakim.issuetracker.entity.Role;
import com.alhakim.issuetracker.entity.User;
import com.alhakim.issuetracker.exception.ResourceNotFoundException;
import com.alhakim.issuetracker.repository.RoleRepository;
import com.alhakim.issuetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByIdentity(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Role> roles = roleRepository.findByUserId(user.getId());

        return MyUserDetails.builder().user(user).roles(roles).build();
    }
}

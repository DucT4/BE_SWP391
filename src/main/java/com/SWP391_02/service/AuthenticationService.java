package com.SWP391_02.service;

import com.SWP391_02.dto.*;
import com.SWP391_02.entity.User;                // <-- entity của bạn
import com.SWP391_02.enums.Role;
import com.SWP391_02.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${app.admin.secret:}")
    private String adminSecretConfig;

    private static final Set<Role> ALLOWED_ROLES =
            EnumSet.of(Role.SC_STAFF, Role.SC_TECHNICIAN, Role.SC_MANAGER,
                    Role.EVM_STAFF, Role.EVM_ADMIN);

    // ===== Register =====
    public UserResponse register(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Role requestedRole = (req.getRole() == null) ? Role.SC_STAFF : req.getRole();
        if (!ALLOWED_ROLES.contains(requestedRole)) {
            throw new RuntimeException("Invalid role");
        }

        if (requestedRole == Role.EVM_ADMIN) {
            if (userRepository.existsByRole(Role.EVM_ADMIN)) {
                throw new RuntimeException("EVM_ADMIN already exists");
            }
            if (adminSecretConfig != null && !adminSecretConfig.isBlank()) {
                if (req.getAdminSecret() == null || !req.getAdminSecret().equals(adminSecretConfig)) {
                    throw new RuntimeException("Not allowed to create EVM_ADMIN");
                }
            }
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .role(requestedRole)
                .build();

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    // ===== Login =====
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenService.generateToken(user);
        return new LoginResponse(
                token,
                new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name()
                )
        );
    }


    // ===== UserDetailsService cho Spring Security =====
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name())
                .build();
    }

}

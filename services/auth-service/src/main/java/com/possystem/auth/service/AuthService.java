package com.possystem.auth.service;

import com.possystem.auth.dto.AuthResponse;
import com.possystem.auth.dto.LoginRequest;
import com.possystem.auth.dto.SignupRequest;
import com.possystem.auth.entity.ERole;
import com.possystem.auth.entity.Role;
import com.possystem.auth.entity.User;
import com.possystem.auth.repository.RoleRepository;
import com.possystem.auth.repository.UserRepository;
import com.possystem.auth.security.JwtUtils;
import com.possystem.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse loginUser(LoginRequest request) {
        // 1. Authenticate the user against the database
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // 2. Store authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate the JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Extract user details to return in the response
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new AuthResponse(jwt, userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    public void registerUser(SignupRequest request) {
        // 1. Check if username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // 2. Create new user entity
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // NEVER store plain text!
                .build();

        // 3. Assign roles
        Set<String> strRoles = request.roles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default to CASHIER if no role is specified
            Role cashierRole = roleRepository.findByName(ERole.ROLE_CASHIER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(cashierRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role cashierRole = roleRepository.findByName(ERole.ROLE_CASHIER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(cashierRole);
                }
            });
        }

        user.setRoles(roles);

        // 4. Save to database
        userRepository.save(user);
    }
}
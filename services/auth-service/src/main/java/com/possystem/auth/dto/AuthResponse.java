package com.possystem.auth.dto;

import java.util.List;

public record AuthResponse(
        String token,
        String username,
        String email,
        List<String> roles
) {}

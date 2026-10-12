package com.possystem.catalog.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token? Just continue. Spring Security will reject it later if the route is protected.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // strip "Bearer "

        try {
            Claims claims = jwtUtils.parseToken(token);   // <-- verifies the signature!
            String username = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            // Fixed: Use if/else instead of ternary to avoid generic type inference issues
            List<SimpleGrantedAuthority> authorities;
            if (roles == null) {
                authorities = List.of();
            } else {
                authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Keep the full claims available (userId, etc.) for later use
            authentication.setDetails(claims);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // Invalid / expired / tampered token -> treat as anonymous
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
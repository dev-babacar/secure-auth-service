package com.babacar.secureauthservice.adapter.in.web;

import com.babacar.secureauthservice.domain.port.out.TokenBlacklist;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlacklist tokenBlacklist;
    private final JwtDecoder jwtDecoder;

    public JwtBlacklistFilter(TokenBlacklist tokenBlacklist,
                              JwtDecoder jwtDecoder) {
        this.tokenBlacklist = tokenBlacklist;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                var jwt = jwtDecoder.decode(token);
                String jti = jwt.getId();

                if (jti != null && tokenBlacklist.isBlacklisted(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"error\":\"Token révoqué\"}"
                    );
                    return;
                }
            } catch (Exception e) {
                // Token invalide — laisse Spring Security gérer
            }
        }

        filterChain.doFilter(request, response);
    }
}
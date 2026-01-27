package com.securevault.passwordservice.security;

import com.securevault.passwordservice.service.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/passwords/health",
            "/passwords/generate",
            "/passwords/analyze"
    );

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 🔥 1️⃣ VALIDER LE TOKEN
            if (!jwtTokenUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 🔥 2️⃣ EXTRAIRE L’UTILISATEUR
            String username = jwtTokenUtil.extractUsername(token);

            // 🔥 3️⃣ AUTHENTIFICATION VALIDE
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of() // authorities vides mais NON null
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            logger.error("JWT invalide", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

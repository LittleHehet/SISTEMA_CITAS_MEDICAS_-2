package com.example.sistema_citas.security;

import com.example.sistema_citas.data.UsuarioRepository;
import com.example.sistema_citas.service.Service;
import com.example.sistema_citas.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final Service service;

    private final org.springframework.security.oauth2.jwt.JwtDecoder googleJwtDecoder;
    private final GoogleJwtAuthConverter googleConverter;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         Service service,
                         org.springframework.security.oauth2.jwt.JwtDecoder googleJwtDecoder,
                         UsuarioRepository usuarioRepository) {
        this.jwtUtil = jwtUtil;
        this.service = service;
        this.googleJwtDecoder = googleJwtDecoder;
        this.googleConverter = new GoogleJwtAuthConverter(usuarioRepository);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/login/") || path.startsWith("/api/signup") || path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si ya hay auth, no reproceses
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        // 1) Intentar LOCAL (HS256)
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = service.load(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception ignored) {
            // no era local
        }

        // 2) Intentar GOOGLE (RS256)
        try {
            var jwt = googleJwtDecoder.decode(token);
            var auth = googleConverter.convert(jwt); // JwtAuthenticationToken con ROLE_...
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {
            // no era google tampoco
        }

        filterChain.doFilter(request, response);
    }
}

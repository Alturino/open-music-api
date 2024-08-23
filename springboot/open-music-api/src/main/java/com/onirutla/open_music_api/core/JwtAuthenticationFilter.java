package com.onirutla.open_music_api.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.atDebug()
                    .addKeyValue("auth_header", authHeader)
                    .addKeyValue("auth_header_is_null", authHeader == null)
                    .addKeyValue("auth_header_is_bearer", authHeader != null && authHeader.startsWith("Bearer "))
                    .addKeyValue("process", "authentication")
                    .log();
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);
        if (username == null) {
            log.atDebug()
                    .addKeyValue("auth_header", authHeader)
                    .addKeyValue("username", null)
                    .addKeyValue("process", "authentication")
                    .log();
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        boolean isJwtNotValid = !jwtService.isJwtValid(jwt, userDetails);
        if (userDetails == null || isJwtNotValid) {
            log.atDebug()
                    .addKeyValue("jwt", jwt)
                    .addKeyValue("username", username)
                    .addKeyValue("user_details", userDetails == null ? null : userDetails.getUsername())
                    .addKeyValue("is_jwt_not_valid", isJwtNotValid)
                    .addKeyValue("process", "authentication")
                    .log();
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}

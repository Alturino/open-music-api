package com.alturino.open_music_api.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alturino.open_music_api.user.UserEntity;
import com.alturino.open_music_api.user.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Encoders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final Environment env;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    log.atTrace().log("initiating process authentication");

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.atInfo().log("authentication is not valid");
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = authHeader.split(" ")[1];
    MDC.put("jwt", jwt);
    log.atInfo().log("retrieved jwt");

    String secretKey = env.getProperty("environment.access_token_secret_key");
    String encodedSecretKey = Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    Claims claims = jwtService.getTokenClaims(encodedSecretKey, jwt);
    String username = claims.getSubject();
    if (username == null) {
      log.atInfo().log("username should not be null");
      filterChain.doFilter(request, response);
      return;
    }

    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    MDC.put("user_id", user.getId());
    boolean isJwtNotValid = !jwtService.isAccessTokenValid(jwt, user);
    if (isJwtNotValid) {
      log.atInfo().log("jwt must be valid");
      filterChain.doFilter(request, response);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        user.getId(),
        null,
        user.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }
}

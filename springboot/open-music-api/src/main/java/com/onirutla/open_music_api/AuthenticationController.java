package com.onirutla.open_music_api;

import com.onirutla.open_music_api.core.JwtService;
import com.onirutla.open_music_api.user.LoginRequest;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MapBuilder;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/authentications")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<Map<Object, Object>> login(@RequestBody @Valid LoginRequest request) {
        UserEntity userEntity = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Username or password is incorrect"));

        boolean isValidPassword = passwordEncoder.matches(request.password(), userEntity.getPassword());
        if (!isValidPassword) {
            log.atDebug()
                    .addKeyValue("username", userEntity.getUsername())
                    .addKeyValue("request_username", request.username())
                    .addKeyValue("password", userEntity.getPassword())
                    .addKeyValue("request_password", request.password())
                    .addKeyValue("is_valid_password", false)
                    .addKeyValue("process", "login")
                    .addKeyValue("message", "password is incorrect")
                    .log();
            throw new BadCredentialsException("Username or password is incorrect");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails user = User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .authorities("ROLE_USER")
                .roles("USER")
                .build();

        Map<String, Object> claims = new StringObjectMapBuilder()
                .put("username", userEntity.getUsername())
                .put("password", userEntity.getPassword())
                .put("authorities", user.getAuthorities())
                .get();

        log.atDebug()
                .addKeyValue("process", "login")
                .addKeyValue("message", "initiating access token generation")
                .log();
        String accessToken = jwtService.generateToken(Jwts.claims(claims), user);
        log.atDebug()
                .addKeyValue("process", "login")
                .addKeyValue("message", "finished access token generation")
                .log();

        log.atDebug()
                .addKeyValue("process", "login")
                .addKeyValue("message", "initiating refresh token generation")
                .log();
        String refreshToken = jwtService.generateToken(Jwts.claims(claims), user);
        log.atDebug()
                .addKeyValue("process", "login")
                .addKeyValue("message", "finished refresh token generation")
                .log();

        Map<Object, Object> data = new MapBuilder<>()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .get();
        Map<Object, Object> body = new MapBuilder<>()
                .put("status", "success")
                .put("message", "Login success")
                .put("data", data)
                .get();
        return ResponseEntity.status(200).body(body);
    }

}

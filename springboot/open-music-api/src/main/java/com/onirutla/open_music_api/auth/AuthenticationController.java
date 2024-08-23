package com.onirutla.open_music_api.auth;

import com.onirutla.open_music_api.core.JwtService;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/authentications")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        log.atDebug()
                .addKeyValue("login_request", request.toString())
                .addKeyValue("password_type", request.password().getClass().getTypeName())
                .addKeyValue("process", "login")
                .log();
        UserEntity userEntity = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    BadCredentialsException e = new BadCredentialsException("username is incorrect");
                    log.atError()
                            .setMessage("username not found")
                            .addKeyValue("username", request.username())
                            .setCause(e)
                            .log();
                    return e;
                });

        boolean isValidPassword = passwordEncoder.matches(request.password(), userEntity.getPassword());
        if (!isValidPassword) {
            BadCredentialsException e = new BadCredentialsException("password is incorrect");
            log.atError()
                    .setMessage("password is incorrect")
                    .addKeyValue("username", userEntity.getUsername())
                    .addKeyValue("request_username", request.username())
                    .addKeyValue("password", userEntity.getPassword())
                    .addKeyValue("request_password", request.password())
                    .addKeyValue("is_valid_password", false)
                    .addKeyValue("process", "login")
                    .setCause(e)
                    .log();
            throw e;
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getId(), userEntity.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> claims = new StringObjectMapBuilder()
                .put("username", userEntity.getUsername())
                .put("password", userEntity.getPassword())
                .put("authorities", userEntity.getAuthorities())
                .get();

        log.atDebug()
                .setMessage("initiating access token generation")
                .addKeyValue("process", "login")
                .log();
        String accessToken = jwtService.generateToken(Jwts.claims(claims), userEntity);
        log.atDebug()
                .addKeyValue("process", "login")
                .setMessage("finished access token generation")
                .log();

        log.atDebug()
                .addKeyValue("process", "login")
                .setMessage("initiating refresh token generation")
                .log();
        String refreshToken = jwtService.generateToken(Jwts.claims(claims), userEntity);
        log.atDebug()
                .addKeyValue("process", "login")
                .setMessage("finished refresh token generation")
                .log();

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Login success")
                .put("data", data)
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}

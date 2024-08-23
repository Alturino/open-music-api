package com.onirutla.open_music_api.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "users")
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request) {
        log.atDebug()
                .addKeyValue("request_body", request)
                .log();
        log.atTrace()
                .setMessage("hashing password")
                .log();
        String hashedPassword = passwordEncoder.encode(request.password());
        log.atTrace()
                .setMessage("password hashed")
                .log();
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(hashedPassword)
                .fullname(request.fullname())
                .userRole(UserRole.USER)
                .build();
        log.atTrace()
                .setMessage("saving user")
                .addKeyValue("user_entity", user)
                .log();
        repository.save(user);
        log.atTrace()
                .setMessage("user saved")
                .addKeyValue("user_entity", user)
                .log();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "User created")
                .put("data", Map.ofEntries(Map.entry("userId", user.getId())))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}

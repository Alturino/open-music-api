package com.onirutla.open_music_api.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RequestMapping(value = "users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request) {
        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .log("initiating process register");

        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .addKeyValue("password", request.password())
                .log("hashing password");
        String hashedPassword = passwordEncoder.encode(request.password());
        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .addKeyValue("password", request.password())
                .addKeyValue("hashedPassword", hashedPassword)
                .log("password hashed");

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(hashedPassword)
                .fullname(request.fullname())
                .userRole(UserRole.USER)
                .build();
        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .addKeyValue("password", request.password())
                .addKeyValue("hashedPassword", hashedPassword)
                .addKeyValue("user", user)
                .log("saving user");
        repository.save(user);
        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .addKeyValue("password", request.password())
                .addKeyValue("hashedPassword", hashedPassword)
                .addKeyValue("user", user)
                .log("user saved");

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "User created")
                .put("data", Map.ofEntries(Map.entry("userId", user.getId())))
                .get();
        log.atDebug()
                .addKeyValue("process", "register")
                .addKeyValue("request_body", request)
                .addKeyValue("password", request.password())
                .addKeyValue("hashedPassword", hashedPassword)
                .addKeyValue("user", user)
                .addKeyValue("request_body", request)
                .log("process register ended");
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}

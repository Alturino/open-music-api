package com.onirutla.open_music_api.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<Object, Object>> createUser(@RequestBody @Valid RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(hashedPassword)
                .fullname(request.fullname())
                .build();
        repository.save(user);
        return ResponseEntity.ok(Map.of());
    }
}

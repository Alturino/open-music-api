package com.alturino.open_music_api.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping(value = "users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  @PostMapping
  public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request) {
    log.atTrace().log("hashing password");
    String hashedPassword = passwordEncoder.encode(request.password());
    log.atInfo().log("password hashed");

    UserEntity user = UserEntity.builder()
        .username(request.username())
        .password(hashedPassword)
        .fullname(request.fullname())
        .userRole(UserRole.USER)
        .build();

    log.atTrace().log("saving user to database");
    repository.save(user);
    log.atInfo().log("user saved to database");

    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("message", "User created")
        .put("data", Map.ofEntries(Map.entry("userId", user.getId())))
        .get();
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }
}

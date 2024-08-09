package com.onirutla.open_music_api.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/authentications")
public class AuthController {
  @PostMapping("/")
  public ResponseEntity<Map<Object, Object>> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(new HashMap<>());
  }
}

package com.onirutla.open_music_api.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/authentications")
public class AuthController {
  @PostMapping("/")
  public ResponseEntity<Map<Object, Object>> login(@RequestBody @Valid LoginRequest request) {
    return ResponseEntity.ok(new HashMap<>());
  }
}

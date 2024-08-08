package com.onirutla.open_music_api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank @Size(min = 8) String username,
    @NotBlank @Size(min = 6, max = 64) String password) {
}

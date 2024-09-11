package com.onirutla.open_music_api.auth;

import jakarta.validation.constraints.NotBlank;

public record DeleteTokenRequest(@NotBlank String refreshToken) {}

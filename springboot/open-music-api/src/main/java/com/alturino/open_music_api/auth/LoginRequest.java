package com.alturino.open_music_api.auth;

import com.alturino.open_music_api.core.StrictStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank @JsonDeserialize(using = StrictStringDeserializer.class) String password
) {
}

package com.onirutla.open_music_api.auth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.onirutla.open_music_api.core.StrictStringDeserializer;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank @JsonDeserialize(using = StrictStringDeserializer.class) String password
) {
}

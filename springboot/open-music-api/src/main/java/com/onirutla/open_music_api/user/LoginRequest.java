package com.onirutla.open_music_api.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank @Length(min = 4) String username,
        @NotBlank @Length(min = 6, max = 64) String password
) {
}

package com.onirutla.open_music_api.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest(
    @NotBlank @Length(min = 8) String username,
    @NotBlank @Length(min = 6, max = 64) String password,
    @NotBlank @Length(min = 8, max = 256) String fullname) {
}

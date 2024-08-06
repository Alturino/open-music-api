package com.onirutla.open_music_api.song;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SongRequest(
        @Nullable String albumId,
        @NotBlank String title,
        @Digits(integer = 4, fraction = 0) @Min(1) int year,
        @NotBlank String performer,
        @NotBlank String genre,
        @Digits(integer = 4, fraction = 0) @Min(1) int duration
) {
}

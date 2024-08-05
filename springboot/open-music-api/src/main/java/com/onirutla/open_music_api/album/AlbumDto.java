package com.onirutla.open_music_api.album;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AlbumDto(@NotBlank String name,
                       @Digits(integer = 4, fraction = 0) @Min(1) int year) {
}

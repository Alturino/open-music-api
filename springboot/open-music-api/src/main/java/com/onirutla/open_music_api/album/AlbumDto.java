package com.onirutla.open_music_api.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlbumDto(@NotBlank String name, @Size(max = 4) int year) {
}

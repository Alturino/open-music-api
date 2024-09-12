package com.onirutla.open_music_api.playlist;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.onirutla.open_music_api.core.StrictStringDeserializer;
import jakarta.validation.constraints.NotBlank;

public record PlaylistPostRequest(@NotBlank @JsonDeserialize(using = StrictStringDeserializer.class) String name) {
}

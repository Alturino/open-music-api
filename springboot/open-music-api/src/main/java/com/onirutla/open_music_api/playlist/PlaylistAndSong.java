package com.onirutla.open_music_api.playlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.onirutla.open_music_api.song.SongEntity;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record PlaylistAndSong(
        String id,
        String ownerId,
        String name,
        List<SongEntity> songs
) {
}

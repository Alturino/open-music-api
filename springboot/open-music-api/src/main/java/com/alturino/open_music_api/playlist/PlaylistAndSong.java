package com.alturino.open_music_api.playlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.alturino.open_music_api.song.Song;
import com.alturino.open_music_api.song.SongEntity;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record PlaylistAndSong(
        String id,
        String username ,
        String name,
        List<Song> songs
) {
}

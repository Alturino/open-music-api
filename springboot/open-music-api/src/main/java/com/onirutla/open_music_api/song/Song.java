package com.onirutla.open_music_api.song;

import java.sql.Timestamp;

public record Song(
        String id,
        String title,
        int year,
        String performer,
        String genre,
        int duration,
        String albumId,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}

package com.onirutla.open_music_api.playlist.activity;

import java.sql.Timestamp;

public record PlaylistActivity(
        String id,
        String playlistId,
        String songId,
        String userId,
        Action action,
        Timestamp createdAt,
        Timestamp updatedAt
) {
}

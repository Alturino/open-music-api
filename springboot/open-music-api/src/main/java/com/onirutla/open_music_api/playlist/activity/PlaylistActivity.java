package com.onirutla.open_music_api.playlist.activity;

public record PlaylistActivity(
        String id,
        String playlistId,
        String songId,
        String userId,
        Action action
) {
}

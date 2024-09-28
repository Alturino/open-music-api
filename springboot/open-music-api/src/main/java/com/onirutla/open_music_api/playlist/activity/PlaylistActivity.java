package com.onirutla.open_music_api.playlist.activity;

import java.time.Instant;

public record PlaylistActivity(
        String username,
        String title,
        String action,
        Instant time
) {
}

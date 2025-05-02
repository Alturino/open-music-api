package com.alturino.open_music_api.collaboration;

public record CollaborationRequest(
        String playlistId,
        String userId
) {
}

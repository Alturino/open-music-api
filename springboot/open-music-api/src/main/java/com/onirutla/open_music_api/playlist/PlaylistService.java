package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.song.SongEntity;
import com.onirutla.open_music_api.song.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Transactional
    public PlaylistAndSong getPlaylistAndSongs(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist playlist_id={} user_id={}", playlistId, userId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlist_and_songs")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("founded playlist playlist_id={} user_id={}", playlistId, userId);
        List<SongEntity> songs = songRepository.findSongByOwnerIdAndPlaylistIdAndSongId(userId, playlistId);
        return new PlaylistAndSong(
                playlist.getId(),
                playlist.getOwnerId(),
                playlist.getName(),
                songs
        );
    }
}


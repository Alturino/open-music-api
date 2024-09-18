package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.song.Song;
import com.onirutla.open_music_api.song.SongRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public PlaylistAndSong getPlaylistAndSongs(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist playlist_id={}", playlistId);
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
                .log("found playlist playlist_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isUserHaveAccessToPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("finding songs playlist_id={} user_id={}", playlistId, userId);
        List<Song> songs = songRepository.findSongByOwnerIdAndPlaylistIdAndSongId(userId, playlistId)
                .stream()
                .map((song) -> new Song(song.getId(), song.getTitle(), song.getYear()))
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("songs", songs)
                .log("found songs playlist_id={} user_id={}", playlistId, userId);

        return new PlaylistAndSong(
                playlist.getId(),
                user.getUsername(),
                playlist.getName(),
                songs
        );
    }

    @Transactional
    public boolean deleteSongInPlaylist(String userId, String playlistId, String songId) {
        log.atError()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atError()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("found playlist playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isUserHaveAccessToPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

    }
}


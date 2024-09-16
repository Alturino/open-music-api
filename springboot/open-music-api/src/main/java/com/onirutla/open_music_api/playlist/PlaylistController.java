package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.song.SongEntity;
import com.onirutla.open_music_api.song.SongRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping(value = "/playlists")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> insertPlaylist(@RequestBody @Valid PlaylistPostRequest request) {
        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .log("initiating process insert_playlist with request={}", request);

        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("request", request)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(userId));
            log.atError()
                    .addKeyValue("process", "insert_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("request", request)
                    .setCause(e)
                    .log(e.getMessage());
            return e;
        });
        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("founded user_id={}", userId);

        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("owner_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("inserting playlist for user_id={} with request={}", userId, request);
        PlaylistEntity playlist = PlaylistEntity.builder()
                .name(request.name())
                .ownerId(userId)
                .build();
        playlistRepository.save(playlist);
        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("owner_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("inserted playlist for user_id={} with request={}", userId, request);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlistId", playlist.getId())
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("data", data)
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPlaylists() {
        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .log("initiating process get_playlists");
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("user_id=%s not found".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlists")
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .log("founded user_id={}", userId);

        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .log("finding playlists with user_id={}", userId);
        List<PlaylistResponse> playlists = playlistRepository.findByOwnerId(userId)
                .stream()
                .map((playlist) -> new PlaylistResponse(playlist.getId(), playlist.getName(), authUser.getUsername()))
                .toList();
        if (playlists.isEmpty()) {
            throw new NotFoundException("playlist not found for user_id=%s".formatted(userId));
        }
        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("playlists", playlists)
                .log("founded playlists with user_id={}", userId);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlists", playlists)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> addSongToUserPlaylist(@RequestBody @Valid SongInPlaylistRequest request, @PathVariable String playlistId) {
        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("initiating process add_song_to_playlist");


        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("request", request)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist not found with playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            return e;
        });
        if (!playlist.getOwnerId().equals(userId)) {
            ForbiddenException e = new ForbiddenException("user do not have access to the playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            throw e;
        }

        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("finding song_id={}", request.songId());
        SongEntity song = songRepository.findById(request.songId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song with id=%s not found".formatted(request.songId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            return e;
        });
        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("founded song_id={}", request.songId());

        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("old_playlist_id", song.getPlaylistId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("updating song playlist_id from playlist_id={}, to playlist_id={}", song.getPlaylistId(), playlistId);
        song.setPlaylistId(playlistId);
        songRepository.save(song);
        log.atDebug()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("old_playlist_id", song.getPlaylistId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("updated song playlist_id from playlist_id={}, to playlist_id={}", song.getPlaylistId(), playlistId);

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "updated song playlist_id from playlist_id=%s to playlist_id=%s".formatted(song.getPlaylistId(), playlistId))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> getSongsInPlaylist(@PathVariable String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atDebug()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("initiating process get_songs_in_playlist with playlist_id={}, user_id={}", playlistId, userId);

        log.atDebug()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("finding playlist playlist_id={} and user_id={}", playlistId, userId);
        PlaylistEntity playlist = playlistRepository.findByIdAndOwnerId(playlistId, userId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist not found with playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_songs_in_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("user_id", userId)
                    .log("failed to get get_songs_in_playlist playlist_id={}, user_id={} with error={}", playlistId, userId, e.getMessage());
            return e;
        });
        log.atDebug()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("user_id", userId)
                .log("found playlist playlist_id={} and user_id={}", playlistId, userId);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlist", playlist)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "successe")
                .put("message", "success get playlist_id=%s".formatted(playlistId))
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> deleteSongInPlaylist(@RequestBody @Valid SongInPlaylistRequest request, @PathVariable String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atDebug()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("initiating process delete_song_in_playlist with playlist_id={}, user_id={}", playlistId, userId);

    }
}

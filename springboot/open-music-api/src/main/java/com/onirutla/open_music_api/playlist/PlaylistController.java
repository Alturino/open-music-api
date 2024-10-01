package com.onirutla.open_music_api.playlist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> insertPlaylist(@RequestBody @Valid PlaylistPostRequest request) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("user_id", userId)
                .log("received request to insert request={} by user_id={}", request, userId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("user_id", userId)
                .log("inserting request={} to user_id={}", request, userId);
        PlaylistEntity insertedPlaylist = playlistService.insertPlaylist(request, userId);
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("playlist", insertedPlaylist)
                .addKeyValue("user_id", userId)
                .log("inserted playlist={} with request={} to user_id={}", insertedPlaylist, request, userId);
        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlistId", insertedPlaylist.getId())
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("data", data)
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPlaylists() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .log("received request get_playlists for user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .log("finding playlist for user_id={}", userId);
        List<PlaylistResponse> playlists = playlistService.getPlaylists(userId)
                .stream()
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlists", playlists)
                .log("found playlist for user_id={}", userId);

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
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("received request add_song_to_user_playlist song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("inserting song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);
        PlaylistAndSongEntity playlistAndSongEntity = playlistService.addSongToUserPlaylist(request, playlistId, userId);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_and_song_entity", playlistAndSongEntity)
                .log("inserted song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "inserted song_id=%s to playlist_id=%s by user_id=%s".formatted(request.songId(), playlistId, userId))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> getSongsInPlaylist(@PathVariable String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("received request get_songs_in_playlist playlist_id={} user_id={}", playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("finding playlist and song playlist_id={} user_id={}", playlistId, userId);
        PlaylistAndSong playlistAndSongs = playlistService.getSongsInPlaylist(userId, playlistId);
        log.atInfo()
                .addKeyValue("process", "get_songs_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("found playlist and song playlist_id={} user_id={}", playlistId, userId);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlist", playlistAndSongs)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "success get playlist_id=%s".formatted(playlistId))
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> deleteSongInPlaylist(@RequestBody @Valid SongInPlaylistRequest request, @PathVariable String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("user_id", userId)
                .log("received request delete_song_in_playlist song_id={}, playlist_id={} by user_id={}", request.songId(), playlistId, userId);

        boolean isDeleted = playlistService.deleteSongInPlaylist(userId, playlistId, request.songId());
        if (!isDeleted) {
            Map<String, Object> errorBody = new StringObjectMapBuilder()
                    .put("status", "failed")
                    .put("message", "unknown error when deleting song from playlist_id=%s".formatted(playlistId))
                    .get();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "success delete song_id=%s from playlist_id=%s".formatted(request.songId(), playlistId))
                .get();
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("received request delete_playlist playlist_id={} by user_id={}", playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleting playlist_id={} from user_id={}", playlistId, userId);
        boolean isPlaylistDeleted = playlistService.deletePlaylist(userId, playlistId);
        if (!isPlaylistDeleted) {
            Map<String, Object> errorBody = new StringObjectMapBuilder()
                    .put("status", "failed")
                    .put("message", "unknown error when deleting song from playlist_id=%s".formatted(playlistId))
                    .get();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleting playlist_id={} from user_id={}", playlistId, userId);

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "deleted playlist_id=%s".formatted(playlistId))
                .get();
        return ResponseEntity.ok(body);
    }
}

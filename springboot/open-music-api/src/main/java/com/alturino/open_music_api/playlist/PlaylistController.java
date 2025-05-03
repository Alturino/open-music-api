package com.alturino.open_music_api.playlist;

import com.alturino.open_music_api.core.exception.ForbiddenException;
import com.alturino.open_music_api.core.exception.NotFoundException;
import com.alturino.open_music_api.core.exception.UnauthorizedRequestException;
import com.alturino.open_music_api.song.SongEntity;
import com.alturino.open_music_api.song.SongRepository;
import com.alturino.open_music_api.user.UserEntity;
import com.alturino.open_music_api.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
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

  private final PlaylistRepository playlistRepository;
  private final PlaylistService playlistService;
  private final PlaylistAndSongRepository playlistAndSongRepository;
  private final UserRepository userRepository;
  private final SongRepository songRepository;

  @PostMapping
  public ResponseEntity<Map<String, Object>> insertPlaylist(@RequestBody @Valid PlaylistPostRequest request) {
    log.atTrace().log("starting insertPlaylist request");
    String userId = MDC.get("user_id");

    log.atTrace().log("finding user", userId);
    UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
      UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(userId));
      log.atError().setCause(e).log(e.getMessage());
      return e;
    });
    log.atDebug().log("found user", userId);

    PlaylistEntity playlist = PlaylistEntity.builder()
        .name(request.name())
        .ownerId(userId)
        .build();
    log.atTrace().log("inserting user to database");
    playlistRepository.save(playlist);
    log.atInfo().log("inserted user to database");

    Map<String, Object> data = new StringObjectMapBuilder()
        .put("playlistId", playlist.getId())
        .get();
    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("data", data)
        .get();

    log.atTrace().log("returning response");
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getPlaylists() {
    log.atTrace().log("starting getPlaylists request");
        String userId = MDC.put("user_id", userId);
    MDC.put("user_id", userId);

    log.atTrace().log("finding user");
    UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
      NotFoundException e = new NotFoundException("user_id=%s not found".formatted(userId));
      log.atError().setCause(e).log(e.getMessage());
      return e;
    });
    log.atInfo().log("found user");

    log.atTrace().log("finding playlist");
    List<PlaylistResponse> playlists = playlistRepository.findByOwnerId(userId)
        .stream()
        .map((playlist) -> new PlaylistResponse(playlist.getId(), playlist.getName(), authUser.getUsername()))
        .toList();
    if (playlists.isEmpty()) {
      throw new NotFoundException("playlist not found for user_id=%s".formatted(userId));
    }
    log.atInfo().log("found playlist");

    Map<String, Object> data = new StringObjectMapBuilder()
        .put("playlists", playlists)
        .get();
    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("data", data)
        .get();
    log.atTrace().log("returning response");
    return ResponseEntity.ok(body);
  }

  @PostMapping("/{playlistId}/songs")
  @Transactional
  public ResponseEntity<Map<String, Object>> addSongToUserPlaylist(@RequestBody @Valid SongInPlaylistRequest request,
      @PathVariable String playlistId) {
    log.atInfo().log("initiating process add_song_to_playlist");

        String userId = MDC.put("user_id", userId);

    log.atInfo().log("finding playlist_id={}", playlistId);
    PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
      NotFoundException e = new NotFoundException("playlist not found with playlist_id=%s".formatted(playlistId));
      log.atError()
          .setCause(e)

          .log(e.getMessage());
      return e;
    });
    if (!playlist.getOwnerId().equals(userId)) {
      ForbiddenException e = new ForbiddenException(
          "user do not have access to the playlist_id=%s".formatted(playlistId));
      log.atError()
          .setCause(e)

          .log(e.getMessage());
      throw e;
    }

    log.atInfo()

        .log("finding song_id={}", request.songId());
    SongEntity song = songRepository.findById(request.songId()).orElseThrow(() -> {
      NotFoundException e = new NotFoundException("song with id=%s not found".formatted(request.songId()));
      log.atError()
          .setCause(e)

          .log(e.getMessage());
      return e;
    });
    log.atInfo()

        .log("found song_id={}", request.songId());

    log.atInfo()

        .log("updating song playlist_id from playlist_id={}, to playlist_id={}", request.songId(), playlistId);
    PlaylistAndSongEntity playlistAndSongEntity = PlaylistAndSongEntity.builder()
        .playlistId(playlistId)
        .songId(request.songId())
        .build();
    playlistAndSongRepository.save(playlistAndSongEntity);
    log.atInfo()

        .log("updated song playlist_id from playlist_id={}, to playlist_id={}", request.songId(), playlistId);

    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("message",
            "updated song playlist_id from playlist_id=%s to playlist_id=%s".formatted(request.songId(), playlistId))
        .get();
    return ResponseEntity.status(HttpStatus.CREATED).body(body);
  }

  @GetMapping("/{playlistId}/songs")
  public ResponseEntity<Map<String, Object>> getSongsInPlaylist(@PathVariable String playlistId) {
    log.atInfo()

        .log("initiating process get_songs_in_playlist with playlist_id={}", playlistId);

    log.atInfo()

        .log("retrieving user_id from authentication");
        String userId = MDC.put("user_id", userId);
    log.atInfo()

        .log("retrieved user_id={} from authentication", userId);

    log.atInfo()

        .log("finding playlist and song playlist_id={} user_id={}", playlistId, userId);
    PlaylistAndSong playlistAndSongs = playlistService.getPlaylistAndSongs(userId, playlistId);
    log.atInfo()

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
  public ResponseEntity<Map<String, Object>> deleteSongInPlaylist(@RequestBody @Valid SongInPlaylistRequest request,
      @PathVariable String playlistId) {
    log.atInfo()

        .log("initiating process delete_song_in_playlist with playlist_id={}", playlistId);

    log.atInfo()

        .log("retrieving user_id from authentication");
        String userId = MDC.put("user_id", userId);
    log.atInfo()

        .log("retrieved user_id={} from authentication", userId);

    boolean isDeleted = playlistService.deleteSongInPlaylist(userId, playlistId, request.songId());
    if (!isDeleted) {
      Map<String, Object> errorBody = new StringObjectMapBuilder()
          .put("status", "failed")
          .put("message", "unknown error when deleting song from playlist with playlist_id=%s".formatted(playlistId))
          .get();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }

    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("message",
            "success delete song with song_id=%s from playlist with playlist_id=%s".formatted(request.songId(),
                playlistId))
        .get();
    return ResponseEntity.ok(body);
  }

  @DeleteMapping("/{playlistId}")
  public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable String playlistId) {
    log.atInfo()

        .log("initiating process delete_playlist with playlist_id={}", playlistId);

    log.atInfo()

        .log("retrieving user_id from authentication");
        String userId = MDC.put("user_id", userId);
    log.atInfo()

        .log("retrieved user_id={} from authentication", userId);

    log.atInfo()

        .log("deleting playlist with playlist_id={}", playlistId);
    boolean isPlaylistDeleted = playlistService.deletePlaylist(userId, playlistId);
    if (!isPlaylistDeleted) {
      Map<String, Object> errorBody = new StringObjectMapBuilder()
          .put("status", "failed")
          .put("message", "unknown error when deleting song from playlist with playlist_id=%s".formatted(playlistId))
          .get();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
    log.atInfo()

        .log("deleted playlist with playlist_id={}", playlistId);

    Map<String, Object> body = new StringObjectMapBuilder()
        .put("status", "success")
        .put("message", "deleted playlist with playlist_id=%s".formatted(playlistId))
        .get();
    return ResponseEntity.ok(body);
  }
}

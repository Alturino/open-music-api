package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.NotFoundException;
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
                .addKeyValue("owner_id", userId)
                .addKeyValue("request", request)
                .log("validating if authenticated user exists");
        UserEntity authUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("authenticated user is not exists");
                    log.atError()
                            .addKeyValue("process", "insert_playlist")
                            .addKeyValue("user_id", userId)
                            .addKeyValue("owner_id", userId)
                            .addKeyValue("request", request)
                            .setCause(e)
                            .log(e.getMessage());
                    return e;
                });
        log.atDebug()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("owner_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("validated authenticated user exists");

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
                .put("ownerId", playlist.getOwnerId())
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
                .log("validating if authenticated user exists");
        UserEntity authUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    NotFoundException e = new NotFoundException("authenticated user not found");
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
                .log("validated authenticated user exists");

        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .log("finding playlists for user_id={}", userId);
        List<PlaylistEntity> playlists = playlistRepository.findByUserId(userId);
        if (playlists.isEmpty()) {
            throw new NotFoundException(String.format("playlist not found for user_id=%s", userId));
        }
        log.atDebug()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("playlists", playlists)
                .log("founded playlists for user_id={}", userId);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlists", playlists)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }
}

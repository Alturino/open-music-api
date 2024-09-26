package com.onirutla.open_music_api.playlist.activity;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/playlists/{playlistId}/activities")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PlaylistActivityController {

    private final PlaylistActivityService playlistActivityService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActivitiesInPlaylist(@PathVariable(name = "playlistId") String playlistId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("received request get_activities_in_playlist by user_id={} for playlist_id={}", userId, playlistId);


        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("finding activities with playlist_id={} and user_id={}", playlistId, userId);
        List<PlaylistActivity> activities = playlistActivityService.getActivitiesInPlaylist(playlistId, userId);
        if (activities.isEmpty()) {
            ForbiddenException e = new ForbiddenException("user_id=%s is forbidden to access playlist".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            throw e;
        }
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("activities", activities)
                .log("found activities with playlist_id={} and user_id={}", playlistId, userId);

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("playlistId", playlistId)
                .put("activities", activities)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }
}

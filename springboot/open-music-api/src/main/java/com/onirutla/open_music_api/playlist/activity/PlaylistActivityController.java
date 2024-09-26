package com.onirutla.open_music_api.playlist.activity;

import com.onirutla.open_music_api.core.exception.NotFoundException;
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

    private final PlaylistActivityRepository playlistActivityRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActivitiesInPlaylist(@PathVariable(name = "playlistId") String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlistId", playlistId)
                .log("initiating process get_activities_in_playlist");

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlistId", playlistId)
                .log("retrieving userId");
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlistId", playlistId)
                .addKeyValue("userId", userId)
                .log("retrieved userId={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlistId", playlistId)
                .addKeyValue("userId", userId)
                .log("finding activities with playlist_id={} and user_id={}", playlistId, userId);
        List<PlaylistActivity> activities = playlistActivityRepository.findPlaylistActivityEntitiesByPlaylistIdAndUserId(playlistId, userId)
                .stream()
                .map((activity) -> new PlaylistActivity(activity.getId(), activity.getPlaylistId(), activity.getSongId(), activity.getUserId(), activity.getAction()))
                .toList();
        if (activities.isEmpty()) {
            NotFoundException e = new NotFoundException("activities for playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("playlistId", playlistId)
                    .addKeyValue("userId", userId)
                    .log(e.getMessage());
            throw e;
        }
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlistId", playlistId)
                .addKeyValue("userId", userId)
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
        return ResponseEntity.internalServerError().body(body);
    }
}

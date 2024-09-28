package com.onirutla.open_music_api.playlist.activity;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.playlist.PlaylistEntity;
import com.onirutla.open_music_api.playlist.PlaylistRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaylistActivityService {

    private final PlaylistActivityRepository playlistActivityRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public List<PlaylistActivity> getActivitiesInPlaylist(String playlistId, String userId) {
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("initiating process get_activities_in_playlist");

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s is unauthorized".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_activities_in_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_activities_in_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("user_id", userId)
                    .addKeyValue("user", user)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist", playlist)
                .log("found playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("finding activities with playlist_id={} and user_id={}", playlistId, userId);
        List<PlaylistActivity> activities = playlistActivityRepository.findPlaylistActivityEntitiesByPlaylistIdAndUserId(playlistId, userId)
                .stream()
                .map((object) -> new PlaylistActivity(
                        object.get("username").toString(),
                        object.get("title").toString(),
                        object.get("playlist_activity_action").toString().toLowerCase(Locale.ROOT),
                        ((Timestamp) object.get("created_at")).toInstant()
                ))
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("activities", activities)
                .log("found activities with playlist_id={} and user_id={}", playlistId, userId);

        return activities;
    }
}

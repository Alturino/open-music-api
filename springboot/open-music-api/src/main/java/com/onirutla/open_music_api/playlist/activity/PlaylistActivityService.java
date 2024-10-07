package com.onirutla.open_music_api.playlist.activity;

import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.playlist.PlaylistEntity;
import com.onirutla.open_music_api.playlist.PlaylistRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Slf4j
@Service
public class PlaylistActivityService {

    private final PlaylistActivityRepository playlistActivityRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<PlaylistActivity> getActivitiesInPlaylist(String playlistId, String requesterUserId) {
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_requester_user_id", requesterUserId)
                .log("initiating process get_activities_in_playlist");

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requesterUserId)
                .log("finding requester_user_id={}", requesterUserId);
        UserEntity requesterUser = userRepository.findById(requesterUserId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("requester_user_id=%s is unauthorized".formatted(requesterUserId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_activities_in_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("requester_user_id", requesterUserId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("requester_user", requesterUser)
                .log("found requester_user_id={}", requesterUserId);

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("requester_user", requesterUser)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_activities_in_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("requester_user_id", requesterUserId)
                    .addKeyValue("requester_user", requesterUser)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("requester_user", requesterUser)
                .addKeyValue("playlist", playlist)
                .log("found playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "get_activities_in_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requesterUserId)
                .log("finding activities with playlist_id={} and requester_user_id={}", playlistId, requesterUserId);
        List<PlaylistActivity> activities = playlistActivityRepository.findPlaylistActivityEntitiesByPlaylistIdAndUserId(playlistId, requesterUserId)
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
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("activities", activities)
                .log("found activities with playlist_id={} and requester_user_id={}", playlistId, requesterUserId);

        return activities;
    }

    @Transactional
    public PlaylistActivityEntity insertPlaylistActivity(PlaylistActivityEntity playlistActivityEntity, String requesterUserId) {
        log.atInfo()
                .addKeyValue("process", "insert_playlist_activity")
                .addKeyValue("requester_requester_user_id", requesterUserId)
                .log("initiating process insert_playlist_activity");

        log.atInfo()
                .addKeyValue("process", "insert_playlist_activity")
                .addKeyValue("requester_user_id", requesterUserId)
                .log("finding requester_user_id={}", requesterUserId);
        userRepository.findById(requesterUserId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("requester_user_id=%s is unauthorized".formatted(requesterUserId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "insert_playlist_activity")
                    .addKeyValue("requester_user_id", requesterUserId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "insert_playlist_activity")
                .addKeyValue("requester_user_id", requesterUserId)
                .log("found requester_user_id={}", requesterUserId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist_activity")
                .addKeyValue("requester_user_id", requesterUserId)
                .log("inserting playlist_activity by requester_user_id={}", requesterUserId);
        PlaylistActivityEntity savedPlaylistActivity = playlistActivityRepository.save(playlistActivityEntity);
        log.atInfo()
                .addKeyValue("process", "insert_playlist_activity")
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("saved_playlist_activity", savedPlaylistActivity)
                .log("inserted playlist_activity={} by requester_user_id={}", savedPlaylistActivity, requesterUserId);

        return savedPlaylistActivity;
    }
}


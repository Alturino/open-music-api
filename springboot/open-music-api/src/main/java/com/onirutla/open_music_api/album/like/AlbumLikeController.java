package com.onirutla.open_music_api.album.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping(value = "/albums/{albumId}/likes", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@RestController
@Slf4j
public class AlbumLikeController {

    private final AlbumLikeAndUserService albumLikeAndUserService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> likeAlbum(@PathVariable(name = "albumId") String albumId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("received request by user_id={} to like album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process like_album");
        albumLikeAndUserService.likeAlbum(userId, albumId);
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finished process like_album");

        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "completed like album_id=%s by user_id=%s".formatted(albumId, userId))
                .get();
        return ResponseEntity.ok(body);
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getAlbumLikeCount(@PathVariable(name = "albumId") String albumId) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("received request by user_id={} to get_album_like_count album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process get_album_like_count");
        albumLikeAndUserService.getAlbumLikeCount(userId, albumId);
        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finished process get_album_like_count");


        Map<String, Object> likes = new StringObjectMapBuilder()
                .put("likes", "success")
                .get();
        Map<String, Object> data = new StringObjectMapBuilder()
                .put("data", likes)
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("data", data)
                .put("message", "completed get_album_like_count album_id=%s by user_id=%s".formatted(albumId, userId))
                .put("status", "success")
                .get();

        return ResponseEntity.ok(body);
    }

}
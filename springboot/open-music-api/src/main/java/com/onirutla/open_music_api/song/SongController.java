package com.onirutla.open_music_api.song;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MapBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "songs", produces = {MediaType.APPLICATION_JSON_VALUE})
public class SongController {

    private final SongRepository repository;

    SongController(SongRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Map<Object, Object>> getSongs() {
        List<SongEntity> songs = repository.findAll();
        Map<Object, Object> body = new MapBuilder<>()
                .put("status", "success")
                .put("message", "Song not empty")
                .put("Songs", songs)
                .get();
        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<Object, Object>> getSongDetails(@PathVariable(value = "id") String songId) {
        SongEntity song = repository.findById(songId).orElseThrow();
        Map<Object, Object> body = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is found", songId))
                .put("data", Map.ofEntries(Map.entry("Song", song)))
                .get();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<Object, Object>> insertSong(@Valid @RequestBody SongDto song) {
        SongEntity newSong = SongEntity.builder()
                .title(song.title())
                .genre(song.genre())
                .year(song.year())
                .duration(song.duration())
                .build();
        Map<Object, Object> body = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is inserted", newSong.getId()))
                .put("data", Map.ofEntries(Map.entry("SongId", newSong.getId())))
                .get();
        return ResponseEntity.status(201).body(body);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<Object, Object>> updateSong(@PathVariable(value = "id") String songId,
                                                          @Valid @RequestBody SongDto song) {

        SongEntity oldSong = repository.findById(songId).orElseThrow();
        SongEntity newSong = SongEntity.builder()
                .title(song.title())
                .genre(song.genre())
                .duration(song.duration())
                .year(song.year())
                .createdAt(oldSong.getCreatedAt())
                .build();
        repository.saveAndFlush(newSong);
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is updated", songId))
                .put("data", newSong)
                .get();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<Object, Object>> deleteSong(@PathVariable(value = "id") String songId) {
        repository.findById(songId).orElseThrow();
        repository.deleteById(songId);
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is deleted", songId))
                .get();
        return ResponseEntity.ok(response);
    }

}

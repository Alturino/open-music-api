package com.onirutla.open_music_api.song;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = "songs", produces = { MediaType.APPLICATION_JSON_VALUE })
@RequiredArgsConstructor
@RestController
@Slf4j
public class SongController {

    private final SongRepository repository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllSongs(
            @RequestParam(value = "title", required = false) Optional<String> title,
            @RequestParam(value = "performer", required = false) Optional<String> performer
    ) {
        List<SongResponse> songs = repository
                .findSongByTitleOrPerformer(title.orElse(""), performer.orElse(""))
                .stream()
                .map(song -> new SongResponse(song.getId(), song.getTitle(), song.getPerformer()))
                .toList();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Song not empty")
                .put("data", Map.ofEntries(Map.entry("songs", songs)))
                .get();
        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> getSongDetails(@PathVariable(value = "id") String songId) {
        log.atTrace()
                .addKeyValue("songId", songId)
                .addKeyValue("function", "get")
                .log();
        SongEntity song = repository.findById(songId).orElseThrow();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is found", songId))
                .put("data", Map.ofEntries(Map.entry("song", song)))
                .get();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> insertSong(@Valid @RequestBody SongRequest songRequest) {
        SongEntity song = SongEntity.builder()
                .title(songRequest.title())
                .year(songRequest.year())
                .performer(songRequest.performer())
                .genre(songRequest.genre())
                .duration(songRequest.duration())
                .albumId(songRequest.albumId())
                .build();
        SongEntity newSong = repository.save(song);
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is inserted", newSong.getId()))
                .put("data", Map.ofEntries(Map.entry("songId", newSong.getId())))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> updateSong(
            @PathVariable(value = "id") String songId,
            @Valid @RequestBody SongRequest songRequest
    ) {
        SongEntity oldSong = repository.findById(songId).orElseThrow();
        SongEntity newSong = SongEntity.builder()
                .id(oldSong.getId())
                .title(songRequest.title())
                .year(songRequest.year())
                .performer(songRequest.performer())
                .genre(songRequest.genre())
                .duration(songRequest.duration())
                .createdAt(oldSong.getCreatedAt())
                .build();
        SongEntity updatedSong = repository.saveAndFlush(newSong);
        Map<String, Object> response = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is updated", songId))
                .put("data", Map.ofEntries(Map.entry("song", updatedSong)))
                .get();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable(value = "id") String songId) {
        repository.findById(songId).orElseThrow();
        repository.deleteById(songId);
        Map<String, Object> response = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", String.format("Song with id=%s is deleted", songId))
                .get();
        return ResponseEntity.ok(response);
    }

}

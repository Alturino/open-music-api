package com.onirutla.open_music_api.album;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RequestMapping(value = "/albums", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@RestController
@Slf4j
public class AlbumController {

    private final AlbumRepository repository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllAlbums() {
        List<AlbumEntity> albums = repository.findAll();
        if (albums.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No albums found");
        }
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Album not empty")
                .put("albums", albums)
                .get();
        return ResponseEntity.ok(body);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> findAlbumById(@PathVariable(value = "id") String albumId) {
        AlbumEntity album = repository.findById(albumId).orElseThrow();
        log.atInfo().log("album={}", album.toString());
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Album with id=%s is found".formatted(albumId))
                .put("data", Map.ofEntries(Map.entry("album", album)))
                .get();
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> insertAlbum(@Valid @RequestBody AlbumDto album) {
        AlbumEntity newAlbum = repository.save(AlbumEntity.builder()
                                                       .name(album.name())
                                                       .year(album.year())
                                                       .build());
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Album with id=%s is inserted".formatted(newAlbum.getId()))
                .put("data", Map.ofEntries(Map.entry("albumId", newAlbum.getId())))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> updateAlbum(
            @PathVariable(value = "id") String albumId,
            @Valid @RequestBody AlbumDto album
    ) {

        AlbumEntity oldAlbum = repository.findById(albumId).orElseThrow();
        AlbumEntity newAlbum = AlbumEntity.builder()
                .id(albumId)
                .year(album.year())
                .name(album.name())
                .createdAt(oldAlbum.getCreatedAt())
                .build();
        log.atInfo()
                .addKeyValue("albumId", albumId)
                .addKeyValue("oldAlbum", oldAlbum.toString())
                .addKeyValue("newAlbum", newAlbum.toString())
                .log("updateAlbum");
        repository.save(newAlbum);
        Map<String, Object> response = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Album with id=%s is updated".formatted(albumId))
                .put("data", album)
                .get();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(@PathVariable(value = "id") String albumId) {
        repository.findById(albumId).orElseThrow();
        repository.deleteById(albumId);
        Map<String, Object> response = new StringObjectMapBuilder()
                .put("status", "success")
                .put("message", "Album with id=%s is deleted".formatted(albumId))
                .get();
        return ResponseEntity.ok(response);
    }

}

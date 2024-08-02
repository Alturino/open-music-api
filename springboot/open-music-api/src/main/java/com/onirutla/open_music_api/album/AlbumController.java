package com.onirutla.open_music_api.album;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MapBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController("/albums")
@Log4j
public class AlbumController {

    private final AlbumRepository repository;

    AlbumController(AlbumRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Object, Object>> getAlbums() {
        List<AlbumEntity> albums = repository.findAll();
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", "Album not empty")
                .put("albums", albums)
                .get();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Map<Object, Object>> getAlbumDetails(@PathVariable(value = "id") String albumId) {
        AlbumEntity album = repository.findById(albumId).orElseThrow();
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Album with id=%s is found", albumId))
                .put("data", album)
                .get();
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Map<Object, Object>> insertAlbum(@Validated @RequestBody AlbumEntity album) {
        repository.save(album);
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Album with id=%s is inserted", album.getId()))
                .get();
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Map<Object, Object>> updateAlbum(@PathVariable(value = "id") String albumId, @Validated @RequestBody AlbumEntity album) {
        repository.findById(albumId).orElseThrow();
        repository.save(album);
        Map<Object, Object> response = new MapBuilder<>()
                .put("status", "success")
                .put("message", String.format("Album with id=%s is updated", album.getId()))
                .put("data", album)
                .get();
        return ResponseEntity.ok(response);
    }

}

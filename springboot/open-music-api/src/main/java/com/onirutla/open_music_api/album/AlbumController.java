package com.onirutla.open_music_api.album;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController("/albums")
public class AlbumController {

    private final AlbumRepository repository;

    AlbumController(AlbumRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AlbumEntity>> getAlbums() {
        List<AlbumEntity> albums = repository.findAll();
        return ResponseEntity.ok(albums);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AlbumEntity> getAlbumDetails(@PathVariable(value = "id") String albumId) {
        Optional<AlbumEntity> album = repository.findById(albumId);
        return ResponseEntity.of(album);
    }

    @PostMapping(value = "/")
    public ResponseEntity<AlbumEntity> insertAlbum(@RequestBody AlbumEntity album) {
        return ResponseEntity.ok(repository.save(album));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AlbumEntity> updateAlbum(@PathVariable(value = "id") String albumId, @RequestBody AlbumEntity album) {
        repository.findById(albumId).orElseThrow();
        repository.save(album);
        return ResponseEntity.ok(album);
    }

}

package com.onirutla.open_music_api.album;

import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController("/albums")
public class AlbumController {

    private final AlbumRepository repository;

    AlbumController(AlbumRepository repository) {
        this.repository = repository;
    }

    public List<AlbumEntity> getAlbums() {
        return repository.findAll();
    }

    public Optional<AlbumEntity> getAlbumDetails(String albumId) {
        return repository.findById(albumId);
    }

}

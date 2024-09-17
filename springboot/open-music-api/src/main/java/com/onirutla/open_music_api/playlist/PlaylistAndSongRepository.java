package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistAndSongRepository extends JpaRepository<PlaylistAndSongEntity, String> {
}

package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistAndSongRepository extends JpaRepository<PlaylistAndSongEntity, String> {
    Optional<PlaylistAndSongEntity> deleteByPlaylistIdAndSongId(String playlistId, String songId);

    Optional<PlaylistAndSongEntity> deleteByPlaylistId(String playlistId);
}

package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistAndSongRepository extends JpaRepository<PlaylistAndSongEntity, String> {
    @Query(value = "delete from playlists_and_songs as ps where ps.playlist_id = :playlistId and ps.song_id = :songId returning ps.*", nativeQuery = true)
    Optional<PlaylistAndSongEntity> deletePlaylistAndSongEntityByPlaylistIdAndSongId(String playlistId, String songId);

    @Query(value = "delete from playlists_and_songs as ps where ps.playlist_id = :playlistId returning ps.*", nativeQuery = true)
    Optional<PlaylistAndSongEntity> deletePlaylistAndSongEntityByPlaylistId(String playlistId);
}

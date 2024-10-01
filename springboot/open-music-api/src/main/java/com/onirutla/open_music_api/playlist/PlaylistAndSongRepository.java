package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistAndSongRepository extends JpaRepository<PlaylistAndSongEntity, String> {
    @Modifying
    @Query(value = "delete from playlists_and_songs as ps where ps.playlist_id = :playlistId and ps.song_id = :songId returning ps.*", nativeQuery = true)
    void deletePlaylistAndSongEntityByPlaylistIdAndSongId(String playlistId, String songId);

    @Modifying
    @Query(value = "delete from playlists_and_songs as ps where ps.playlist_id = :playlistId returning ps.*", nativeQuery = true)
    void deletePlaylistAndSongEntityByPlaylistId(String playlistId);
}

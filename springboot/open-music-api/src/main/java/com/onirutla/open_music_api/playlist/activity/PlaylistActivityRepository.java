package com.onirutla.open_music_api.playlist.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistActivityRepository extends JpaRepository<PlaylistActivityEntity, String> {
    @Query("select pa from playlists as p inner join playlist_activities as pa on p.id = pa.playlistId where p.id = :playlistId and p.ownerId = :userId")
    List<PlaylistActivityEntity> findPlaylistActivityEntitiesByPlaylistIdAndUserId(String playlistId, String userId);
}

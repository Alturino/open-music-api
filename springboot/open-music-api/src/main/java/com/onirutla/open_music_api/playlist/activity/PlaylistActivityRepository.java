package com.onirutla.open_music_api.playlist.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistActivityRepository extends JpaRepository<PlaylistActivity, String> {
    @Query("select pa from playlists as p inner join playlist_activities as pa on p.id = pa.playlistId where p.ownerId = :userId")
    List<PlaylistActivity> findByPlaylistIdAndUserId(String playlistId, String userId);
}

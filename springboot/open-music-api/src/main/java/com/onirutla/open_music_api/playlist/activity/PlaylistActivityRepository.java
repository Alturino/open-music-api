package com.onirutla.open_music_api.playlist.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlaylistActivityRepository extends JpaRepository<PlaylistActivityEntity, String> {
    @Query("""
                select
                    u.username as username,
                    s.title as title,
                    pa.playlistActivityAction as playlist_activity_action,
                    pa.createdAt as created_at
                from playlists as p
                inner join playlist_activities as pa on p.id = pa.playlistId
                inner join songs as s on pa.songId = s.id
                inner join users as u on p.ownerId = u.id
                left join collaborations as c on p.id = c.playlistId
                where p.id = :playlistId and (p.ownerId = :userId or c.collaboratorId = :userId)
            """
    )
    List<Map<String, Object>> findPlaylistActivityEntitiesByPlaylistIdAndUserId(String playlistId, String userId);
}

package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, String> {
    List<PlaylistEntity> findByOwnerId(String ownerId);

    @Query("select p from playlists as p left join collaborations as c on p.id = c.playlistId where c.collaboratorId = :userId or p.ownerId = :userId")
    List<PlaylistEntity> findByUserId(String userId);
}

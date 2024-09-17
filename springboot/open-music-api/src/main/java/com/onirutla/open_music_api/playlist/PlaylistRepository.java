package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.song.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, String> {
    @Query("select p from playlists as p left join collaborations as c on p.id = c.playlistId where p.id = :id and (c.collaboratorId = :ownerId or p.ownerId = :ownerId)")
    Optional<PlaylistEntity> findByIdAndOwnerId(String id, String ownerId);

    @Query("select p from playlists as p left join collaborations as c on p.id = c.playlistId where c.collaboratorId = :ownerId or p.ownerId = :ownerId")
    List<PlaylistEntity> findByOwnerId(String ownerId);
}

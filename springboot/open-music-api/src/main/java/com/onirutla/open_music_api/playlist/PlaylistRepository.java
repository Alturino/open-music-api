package com.onirutla.open_music_api.playlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, String> {
    @Query("select p from playlists as p left join collaborations as c on p.id = c.playlistId where p.id = :id and (c.collaboratorId = :ownerId or p.ownerId = :ownerId)")
    Optional<PlaylistEntity> findByIdAndOwnerId(String id, String ownerId);

    @Query("""
                    select
                        p.id as id,
                        p.name as name,
                        u.username as username
                     from playlists as p
                     left join collaborations as c on p.id = c.playlistId
                     left join users as u on p.ownerId = u.id
                     where c.collaboratorId = :ownerId or p.ownerId = :ownerId
            """)
    List<Map<String, Object>> findByOwnerId(String ownerId);
}

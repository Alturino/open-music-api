package com.onirutla.open_music_api.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, String> {
    @Query("""
            select s
            from songs as s
            where s.title ilike '%:title%' and s.performer ilike '%:performer%'
            """
    )
    List<SongEntity> findSongByTitleOrPerformer(@Param("title") String title, @Param("performer") String performer);

    @Query("""
            select s
            from playlists as p
            left join users as u on p.ownerId = u.id
            left join playlists_and_songs as ps on p.id = ps.playlistId
            left join songs as s on ps.songId = s.id
            left join collaborations as c on p.id = c.playlistId
            where ps.playlistId = :playlistId and (p.ownerId = :userId or c.collaboratorId = :userId)
            """
    )
    List<SongEntity> findSongByOwnerIdAndPlaylistIdAndSongId(String ownerId, String playlistId);
}

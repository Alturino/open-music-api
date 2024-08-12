package com.onirutla.open_music_api.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, String> {
    @Query("""
            select
                s
            from
                songs as s
            where
                s.title ilike %:title% and
                s.performer ilike %:performer%
            """)
    List<SongEntity> findSongByTitleOrPerformer(@Param("title") String title, @Param("performer") String performer);
}

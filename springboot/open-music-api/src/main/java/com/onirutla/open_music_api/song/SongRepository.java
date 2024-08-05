package com.onirutla.open_music_api.song;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, String> {
}

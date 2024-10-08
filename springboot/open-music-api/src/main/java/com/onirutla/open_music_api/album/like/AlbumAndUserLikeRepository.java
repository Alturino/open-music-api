package com.onirutla.open_music_api.album.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumAndUserLikeRepository extends JpaRepository<AlbumAndUserLikeEntity, String> {
    Optional<AlbumAndUserLikeEntity> findByAlbumIdAndUserId(String albumId, String userId);
}

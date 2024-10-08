package com.onirutla.open_music_api.album.like;

import com.onirutla.open_music_api.album.AlbumEntity;
import com.onirutla.open_music_api.album.AlbumRepository;
import com.onirutla.open_music_api.core.exception.BadRequestException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlbumLikeAndUserService {

    private final AlbumAndUserLikeRepository albumAndUserLikeRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likeAlbum(String albumId, String userId) {
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process like_album by user_id={} to album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s is not authorized".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "like_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding album_id={}", albumId);
        AlbumEntity album = albumRepository.findById(albumId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("album_id=%s not found".formatted(albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "like_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("found album_id={}", albumId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("check if user_id={} is already liked album_id={}", userId, albumId);
        albumAndUserLikeRepository.findByAlbumIdAndUserId(albumId, userId).ifPresent((albumAndUserLikeEntity) -> {
            throw new BadRequestException("user_id=%s already liked album_id=%s".formatted(
                    albumAndUserLikeEntity.getUserId(),
                    albumAndUserLikeEntity.getAlbumId()
            ));
        });
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("checked user_id={} is not liked album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("inserting like from user_id={} to album_id={}", userId, albumId);
        AlbumAndUserLikeEntity like = AlbumAndUserLikeEntity.builder()
                .albumId(albumId)
                .userId(userId)
                .build();
        albumAndUserLikeRepository.save(like);
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("inserted like from user_id={} to album_id={}", userId, albumId);

        int oldLikeCount = album.getLikeCount();
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("old_like_count", oldLikeCount)
                .log("increasing count album_id={} from like_count={}", albumId, oldLikeCount);
        int newLikeCount = oldLikeCount + 1;
        album.setLikeCount(newLikeCount);
        albumRepository.save(album);
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("old_like_count", oldLikeCount)
                .addKeyValue("new_like_count", newLikeCount)
                .log("increased count album_id={} from old_like_count={} to new_like_count={}", albumId, oldLikeCount, newLikeCount);
    }

    @Transactional
    public int getAlbumLikeCount(String userId, String albumId) {
        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process get_album_like_count by user_id={} to album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s is not authorized".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "like_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding album_id={}", albumId);
        AlbumEntity album = albumRepository.findById(albumId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("album_id=%s not found".formatted(albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "like_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("found album_id={}", albumId);
    }
}

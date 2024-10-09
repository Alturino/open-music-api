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
            BadRequestException e = new BadRequestException("user_id=%s already liked album_id=%s".formatted(userId, albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "like_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            throw e;
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

        int increasedLikeCount = album.getLikeCount();
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", increasedLikeCount)
                .log("increasing count album_id={} from like_count={}", albumId, increasedLikeCount);
        int newLikeCount = increasedLikeCount + 1;
        album.setLikeCount(newLikeCount);
        albumRepository.save(album);
        log.atInfo()
                .addKeyValue("process", "like_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", increasedLikeCount)
                .addKeyValue("increased_like_count", newLikeCount)
                .log("increased count album_id={} from initial_like_count={} to increased_like_count={}", albumId, increasedLikeCount, newLikeCount);
    }

    @Transactional
    public int getAlbumLikeCount(String userId, String albumId) {
        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process get_album_like_count by user_id={} to album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding album_id={}", albumId);
        AlbumEntity album = albumRepository.findById(albumId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("album_id=%s not found".formatted(albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_album_like_count")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_album_like_count")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("like_count", album.getLikeCount())
                .log("found album_id={}", albumId);

        return album.getLikeCount();
    }

    @Transactional
    public void unlikeAlbum(String userId, String albumId) {
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("initiating process like_album by user_id={} to album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s is not authorized".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "unlike_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .log("finding album_id={}", albumId);
        AlbumEntity album = albumRepository.findById(albumId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("album_id=%s not found".formatted(albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "unlike_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        int initialLikeCount = album.getLikeCount();
        int decreasedLikeCount = initialLikeCount - 1;
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("found album_id={}", albumId);

        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("check if user_id={} is already liked album_id={}", userId, albumId);
        AlbumAndUserLikeEntity albumLike = albumAndUserLikeRepository.findByAlbumIdAndUserId(albumId, userId).orElseThrow(() -> {
            BadRequestException e = new BadRequestException("user_id=%s have not liked the album_id=%s".formatted(userId, albumId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "unlike_album")
                    .addKeyValue("album_id", albumId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("checked user_id={} is not liked album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("deleting like from user_id={} for album_id={}", userId, albumId);
        albumAndUserLikeRepository.delete(albumLike);
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("deleted like from user_id={} for album_id={}", userId, albumId);

        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("decreasing like count for album_id={}", albumId);
        album.setLikeCount(decreasedLikeCount);
        albumRepository.save(album);
        log.atInfo()
                .addKeyValue("process", "unlike_album")
                .addKeyValue("album_id", albumId)
                .addKeyValue("user_id", userId)
                .addKeyValue("initial_like_count", initialLikeCount)
                .addKeyValue("decreased_like_count", decreasedLikeCount)
                .log("decreased like_count from like_count={} to like_count={} album_id={}", initialLikeCount, decreasedLikeCount, albumId);
    }
}

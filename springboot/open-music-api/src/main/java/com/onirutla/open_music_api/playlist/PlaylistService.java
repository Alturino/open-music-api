package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityAction;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityEntity;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityRepository;
import com.onirutla.open_music_api.song.Song;
import com.onirutla.open_music_api.song.SongEntity;
import com.onirutla.open_music_api.song.SongRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistActivityRepository playlistActivityRepository;
    private final PlaylistAndSongRepository playlistAndSongRepository;

    @Transactional
    public PlaylistAndSong getSongsInPlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlist_and_songs")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("found playlist playlist_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checking if user_id={} have access to playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s is forbidden to access playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checked user_id={} have access to playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("finding songs playlist_id={} user_id={}", playlistId, userId);
        List<Song> songs = songRepository.findSongByOwnerIdAndPlaylistIdAndSongId(userId, playlistId)
                .stream()
                .map((song) -> new Song(song.getId(), song.getTitle(), song.getPerformer()))
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("songs", songs)
                .log("found songs playlist_id={} user_id={}", playlistId, userId);

        return new PlaylistAndSong(
                playlist.getId(),
                user.getUsername(),
                playlist.getName(),
                songs
        );
    }

    @Transactional
    public boolean deleteSongInPlaylist(String userId, String playlistId, String songId) {
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("found playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("finding song song_id={}", songId);
        SongEntity song = songRepository.findById(songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("playlist", playlist)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song", song)
                .addKeyValue("song_id", songId)
                .log("found song song_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .log("checking if user_id={} have delete access to playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isHaveDeleteAccess(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s is forbidden to delete playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .addKeyValue("playlist", playlist)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .log("checked user_id={} have delete access to playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .log("deleting song_id={} from playlist_id={}", songId, playlistId);
        playlistAndSongRepository.deletePlaylistAndSongEntityByPlaylistIdAndSongId(playlistId, songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("user", user)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("playlist", playlist)
                    .addKeyValue("song_id", songId)
                    .addKeyValue("song", song)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .log("deleted song_id={} from playlist_id={}", songId, playlistId);

        PlaylistActivityEntity playlistActivity = PlaylistActivityEntity.builder()
                .songId(songId)
                .playlistId(playlistId)
                .userId(userId)
                .playlistActivityAction(PlaylistActivityAction.DELETE)
                .build();
        log.atInfo()
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .addKeyValue("playlist_activity", playlistActivity)
                .log("inserting playlist_activity={}", playlistActivity);
        PlaylistActivityEntity insertedActivity = playlistActivityRepository.save(playlistActivity);
        log.atInfo()
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .addKeyValue("playlist_activity", insertedActivity)
                .log("inserted playlist_activity={}", insertedActivity);

        return true;
    }

    @Transactional
    public boolean deletePlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("initiating deletion of playlist_id={} by user_id={}", playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("checking if user_id={} have access to playlist_id={}", userId,
                     playlistId
                );
        userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s do not have access to the playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("checked user_id={} have access to playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleting playlist from playlist_and_playlist_id={}", playlistId);
        playlistAndSongRepository.deletePlaylistAndSongEntityByPlaylistId(playlistId);
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleted playlist from playlists_and_songs with playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleting playlist from playlists with playlist_id={}", playlistId);
        playlistRepository.deleteById(playlistId);
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleted playlist from playlists with playlist_id={}", playlistId);

        return true;
    }

    @Transactional
    public PlaylistEntity insertPlaylist(PlaylistPostRequest request, String userId) {
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("user_id", userId)
                .log("initiating process insert_request={} by user_id={}", request, userId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(userId));
            log.atError()
                    .addKeyValue("process", "insert_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("request", request)
                    .setCause(e)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("owner_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("inserting playlist for user_id={} with request={}", userId, request);
        PlaylistEntity playlist = PlaylistEntity.builder()
                .name(request.name())
                .ownerId(userId)
                .build();
        PlaylistEntity insertedPlaylist = playlistRepository.save(playlist);
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("owner_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("inserted playlist for user_id={} with request={}", userId, request);
        return insertedPlaylist;
    }

    @Transactional
    public List<PlaylistResponse> getPlaylists(String userId) {
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .log("initiating process get_playlists");

        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("user_id=%s not found".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlists")
                    .addKeyValue("user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .log("finding playlists with user_id={}", userId);
        List<PlaylistResponse> playlists = playlistRepository.findByOwnerId(userId)
                .stream()
                .map((map) -> new PlaylistResponse(map.get("id").toString(), map.get("name").toString(), map.get("username").toString()))
                .toList();
        if (playlists.isEmpty()) {
            throw new NotFoundException("playlist not found for user_id=%s".formatted(userId));
        }
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("playlists", playlists)
                .log("found playlists with user_id={}", userId);

        return playlists;
    }

    @Transactional
    public PlaylistAndSongEntity addSongToUserPlaylist(
            SongInPlaylistRequest request,
            String playlistId,
            String userId
    ) {
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .addKeyValue("song_id", request.songId())
                .log("initiating process add_song_to_playlist song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist not found with playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", request.songId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("found playlist_id={}", playlistId);


        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("finding song_id={}", request.songId());
        SongEntity song = songRepository.findById(request.songId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("id=%s not found".formatted(request.songId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", request.songId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("song", song)
                .log("found song_id={}", request.songId());

        PlaylistAndSongEntity playlistAndSong = PlaylistAndSongEntity.builder()
                .playlistId(playlistId)
                .songId(request.songId())
                .build();
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_and_song", playlistAndSong)
                .addKeyValue("song", song)
                .log("inserting song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);
        PlaylistAndSongEntity insertedPlaylistAndSong = playlistAndSongRepository.save(playlistAndSong);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("old_playlist_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("song", song)
                .addKeyValue("playlist_and_song", insertedPlaylistAndSong)
                .log("inserted song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, userId);

        PlaylistActivityEntity playlistActivity = PlaylistActivityEntity.builder()
                .playlistId(playlistId)
                .songId(request.songId())
                .userId(userId)
                .playlistActivityAction(PlaylistActivityAction.ADD)
                .build();
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist_activity", playlistActivity)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("song", song)
                .log("inserting playlist_activity={} by user_id={} adding song_id={} to playlist_id={}", playlistActivity, userId, request.songId(), playlistId);
        PlaylistActivityEntity insertedPlaylistActivity = playlistActivityRepository.save(playlistActivity);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_activity", insertedPlaylistActivity)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("song", song)
                .log("inserted playlist_activity={} by user_id={} adding song_id={} to playlist_id={}", playlistActivity, userId, request.songId(), playlistId);

        return playlistAndSong;
    }

}

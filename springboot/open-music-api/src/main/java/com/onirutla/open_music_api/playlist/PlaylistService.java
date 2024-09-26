package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.song.Song;
import com.onirutla.open_music_api.song.SongEntity;
import com.onirutla.open_music_api.song.SongRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PlaylistAndSongRepository playlistAndSongRepository;

    @Transactional
    public PlaylistAndSong getSongsInPlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
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
                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
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
                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

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
                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
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
                .log("found playlist playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("finding song song_id={}", songId);
        SongEntity song = songRepository.findById(songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song with song_id=%s not found".formatted(songId));
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
                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
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
                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("user", user)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .addKeyValue("song", song)
                .log("deleting song with song_id={} from playlist with playlist_id={}", songId, playlistId);
        playlistAndSongRepository.deletePlaylistAndSongEntityByPlaylistIdAndSongId(playlistId, songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("deleting song with song_id=%s not found".formatted(songId));
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
                .log("deleted song with song_id={} from playlist with playlist_id={}", songId, playlistId);
        return true;
    }

    @Transactional
    public boolean deletePlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("initiating deletion of playlist with playlist_id={} by user with user_id={}", playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user do not have access to the playlist_id=%s".formatted(playlistId));
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
                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);


        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("user_id", userId)
                .log("deleting playlist from playlist_and_song with playlist_id={}", playlistId);
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
    public PlaylistEntity insertPlaylist(PlaylistPostRequest request) {
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .log("initiating process insert_playlist with request={}", request);

        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("request", request)
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
                .map((playlist) -> new PlaylistResponse(playlist.getId(), playlist.getName(), authUser.getUsername()))
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
    public PlaylistAndSongEntity addSongToUserPlaylist(SongInPlaylistRequest request, String playlistId, String userId) {
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("initiating process add_song_to_playlist");

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("request", request)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist not found with playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            return e;
        });
        if (!playlist.getOwnerId().equals(userId)) {
            ForbiddenException e = new ForbiddenException("user do not have access to the playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            throw e;
        }

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("finding song_id={}", request.songId());
        SongEntity song = songRepository.findById(request.songId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song with id=%s not found".formatted(request.songId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("request", request)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("found song_id={}", request.songId());

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("updating song playlist_id from playlist_id={}, to playlist_id={}", request.songId(), playlistId);
        PlaylistAndSongEntity playlistAndSongEntity = PlaylistAndSongEntity.builder()
                .playlistId(playlistId)
                .songId(request.songId())
                .build();
        playlistAndSongRepository.save(playlistAndSongEntity);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("user_id", userId)
                .addKeyValue("old_playlist_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("request", request)
                .log("updated song playlist_id from playlist_id={}, to playlist_id={}", request.songId(), playlistId);
        return playlistAndSongEntity;
    }

}

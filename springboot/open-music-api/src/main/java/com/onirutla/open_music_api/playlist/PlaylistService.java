package com.onirutla.open_music_api.playlist;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityAction;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityEntity;
import com.onirutla.open_music_api.playlist.activity.PlaylistActivityService;
import com.onirutla.open_music_api.song.Song;
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

    private final PlaylistActivityService playlistActivityService;
    private final PlaylistAndSongRepository playlistAndSongRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlaylistAndSong getSongsInPlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlist_and_songs")
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs").addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("found playlist playlist_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("checking if user_id={} have read access to playlist_id={}", userId, playlistId);
        UserEntity owner = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s is do not have read access to  playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .log("checked user_id={} have read access to playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("finding songs playlist_id={} user_id={}", playlistId, userId);
        List<Song> songs = songRepository.findSongByOwnerIdAndPlaylistIdAndSongId(userId, playlistId)
                .stream()
                .map((song) -> new Song(song.getId(), song.getTitle(), song.getPerformer()))
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_playlist_and_songs")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("songs", songs)
                .log("found songs playlist_id={} user_id={}", playlistId, userId);

        return new PlaylistAndSong(
                playlist.getId(),
                owner.getUsername(),
                playlist.getName(),
                songs
        );
    }

    @Transactional
    public boolean deleteSongInPlaylist(String userId, String playlistId, String songId) {
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("found playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("finding song_id={}", songId);
        songRepository.findById(songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("playlist", playlist)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("found song song_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("checking if user_id={} have delete access to song in playlist_id={}", userId, playlistId);
        UserEntity owner = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s is forbidden to delete song in playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .addKeyValue("playlist", playlist)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner_id", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("checked user_id={} have delete access to song in playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner_id", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("deleting song_id={} from playlist_id={}", songId, playlistId);
        playlistAndSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_song_in_playlist")
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("owner_user_id", owner.getId())
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", songId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_song_in_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner_id", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .log("deleted song_id={} from playlist_id={}", songId, playlistId);

        log.atInfo()
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner_id", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .addKeyValue("song_id", songId)
                .log("inserting playlist_activity");
        PlaylistActivityEntity playlistActivity = PlaylistActivityEntity.builder()
                .songId(songId)
                .playlistId(playlistId)
                .userId(userId)
                .playlistActivityAction(PlaylistActivityAction.DELETE)
                .build();
        PlaylistActivityEntity insertedActivity = playlistActivityService.insertPlaylistActivity(playlistActivity, userId);
        log.atInfo()
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("owner_id", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", songId)
                .addKeyValue("playlist_activity_id", insertedActivity.getId())
                .log("inserted playlist_activity={}", insertedActivity);

        return true;
    }

    @Transactional
    public boolean deletePlaylist(String userId, String playlistId) {
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("playlist_id", playlistId)
                .log("initiating deletion of playlist_id={} by user_id={}", playlistId, userId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("find playlist_id={}", playlistId);
        playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("requester_user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("found playlist_id={}", playlistId);


        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("checking if user_id={} have access to delete playlist_id={}", userId, playlistId);
        userRepository.isOwnerPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s do not have access to delete playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("requester_user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("checked user_id={} have access to delete playlist_id={}", userId, playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("deleting playlist from playlist_and_playlist_id={}", playlistId);
        playlistAndSongRepository.deleteByPlaylistId(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_playlist")
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("requester_user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("deleted playlist from playlists_and_songs with playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("deleting playlist from playlists with playlist_id={}", playlistId);
        playlistRepository.deleteById(playlistId);
        log.atInfo()
                .addKeyValue("process", "delete_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", userId)
                .log("deleted playlist from playlists with playlist_id={}", playlistId);

        return true;
    }

    @Transactional
    public PlaylistEntity insertPlaylist(PlaylistPostRequest request, String userId) {
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", userId)
                .log("initiating process insert_request={} by user_id={}", request, userId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(userId));
            log.atError()
                    .addKeyValue("process", "insert_playlist")
                    .addKeyValue("requester_user_id", userId)
                    .addKeyValue("request", request)
                    .setCause(e)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("request", request)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "insert_playlist")
                .addKeyValue("requester_user_id", userId)
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
                .addKeyValue("requester_user_id", userId)
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
                .addKeyValue("requester_user_id", userId)
                .log("initiating process get_playlists");

        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("requester_user_id", userId)
                .log("finding user_id={}", userId);
        UserEntity authUser = userRepository.findById(userId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("user_id=%s not found".formatted(userId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "get_playlists")
                    .addKeyValue("requester_user_id", userId)
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("user", authUser)
                .log("found user_id={}", userId);

        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("user", authUser)
                .log("finding playlists with user_id={}", userId);
        List<PlaylistResponse> playlists = playlistRepository.findByOwnerId(userId)
                .stream()
                .map((map) -> new PlaylistResponse(map.get("id").toString(), map.get("name").toString(), map.get("username").toString()))
                .toList();
        log.atInfo()
                .addKeyValue("process", "get_playlists")
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("user", authUser)
                .addKeyValue("playlists", playlists)
                .log("found playlists with user_id={}", userId);

        return playlists;
    }

    @Transactional
    public PlaylistAndSongEntity addSongToUserPlaylist(
            SongInPlaylistRequest request,
            String playlistId,
            String requester_user_id
    ) {
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("song_id", request.songId())
                .log("initiating process add_song_to_playlist song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, requester_user_id);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .log("finding playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("requester_user_id", requester_user_id)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", request.songId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("playlist", playlist)
                .log("found playlist_id={}", playlistId);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("finding song_id={}", request.songId());
        songRepository.findById(request.songId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song_id=%s not found".formatted(request.songId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("requester_user_id", requester_user_id)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", request.songId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("found song_id={}", request.songId());

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("checking if user_id={} have insert access to song in playlist_id={}", requester_user_id, playlistId);
        UserEntity owner = userRepository.isOwnerOrCollaboratorPlaylist(requester_user_id, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s do not have insert access to playlist_id=%s".formatted(requester_user_id, playlistId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "add_song_to_playlist")
                    .addKeyValue("requester_user_id", requester_user_id)
                    .addKeyValue("playlist_id", playlistId)
                    .addKeyValue("song_id", request.songId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("user", owner.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("checked user_id={} have insert access to song in playlist_id={}", requester_user_id, playlistId);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("inserting song_id={} to playlist_id={} by user_id={}", request.songId(), playlistId, requester_user_id);
        PlaylistAndSongEntity playlistAndSong = PlaylistAndSongEntity.builder()
                .playlistId(playlistId)
                .songId(request.songId())
                .build();
        PlaylistAndSongEntity insertedPlaylistAndSong = playlistAndSongRepository.save(playlistAndSong);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("old_playlist_id", request.songId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .addKeyValue("playlist_and_song_id", insertedPlaylistAndSong.getPlaylistId())
                .log("inserted playlist_and_song_id={} song_id={} to playlist_id={} by user_id={}", insertedPlaylistAndSong.getPlaylistId(), request.songId(), playlistId, requester_user_id);

        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("inserting by user_id={} adding song_id={} to playlist_id={}", requester_user_id, request.songId(), playlistId);
        PlaylistActivityEntity playlistActivity = PlaylistActivityEntity.builder()
                .playlistId(playlistId)
                .songId(request.songId())
                .userId(requester_user_id)
                .playlistActivityAction(PlaylistActivityAction.ADD)
                .build();
        PlaylistActivityEntity insertedPlaylistActivity = playlistActivityService.insertPlaylistActivity(playlistActivity, requester_user_id);
        log.atInfo()
                .addKeyValue("process", "add_song_to_playlist")
                .addKeyValue("requester_user_id", requester_user_id)
                .addKeyValue("playlist_activity_id", insertedPlaylistActivity.getId())
                .addKeyValue("playlist_id", playlistId)
                .addKeyValue("song_id", request.songId())
                .log("inserted playlist_activity={} by user_id={} adding song_id={} to playlist_id={}", playlistActivity, requester_user_id, request.songId(), playlistId);

        return playlistAndSong;
    }

}

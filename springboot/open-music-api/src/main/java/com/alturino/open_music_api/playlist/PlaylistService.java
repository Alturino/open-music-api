package com.alturino.open_music_api.playlist;

import com.alturino.open_music_api.core.exception.ForbiddenException;
import com.alturino.open_music_api.core.exception.NotFoundException;
import com.alturino.open_music_api.song.Song;
import com.alturino.open_music_api.song.SongEntity;
import com.alturino.open_music_api.song.SongRepository;
import com.alturino.open_music_api.user.UserEntity;
import com.alturino.open_music_api.user.UserRepository;
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
    private final PlaylistAndSongRepository playlistAndSongRepository;

    @Transactional
    public PlaylistAndSong getPlaylistAndSongs(String userId, String playlistId) {
        log.atInfo()



                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()




                .log("found playlist playlist_id={}", userId);

        log.atInfo()



                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()



                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

        log.atInfo()




                .log("finding songs playlist_id={} user_id={}", playlistId, userId);
        List<Song> songs = songRepository.findSongByOwnerIdAndPlaylistIdAndSongId(userId, playlistId)
                .stream()
                .map((song) -> new Song(song.getId(), song.getTitle(), song.getPerformer()))
                .toList();
        log.atInfo()





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




                .log("finding playlist playlist_id={}", playlistId);
        PlaylistEntity playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist with playlist_id=%s not found".formatted(playlistId));
            log.atError()
                    .setCause(e)
    
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()





                .log("found playlist playlist_id={}", playlistId);

        log.atInfo()





                .log("finding song song_id={}", songId);
        SongEntity song = songRepository.findById(songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("song with song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
    
    
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()






                .log("found song song_id={}", playlistId);

        log.atInfo()






                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        UserEntity user = userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user with user_id=%s is forbidden to access playlist with playlist_id=%s".formatted(userId, playlistId));
            log.atError()
                    .setCause(e)
    
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()







                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);

        log.atInfo()







                .log("deleting song with song_id={} from playlist with playlist_id={}", songId, playlistId);
        playlistAndSongRepository.deletePlaylistAndSongEntityByPlaylistIdAndSongId(playlistId, songId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("deleting song with song_id=%s not found".formatted(songId));
            log.atError()
                    .setCause(e)
    
    
    
    
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()







                .log("deleted song with song_id={} from playlist with playlist_id={}", songId, playlistId);
        return true;
    }

    @Transactional
    public boolean deletePlaylist(String userId, String playlistId) {
        log.atInfo()



                .log("initiating deletion of playlist with playlist_id={} by user with user_id={}", playlistId, userId);

        log.atInfo()



                .log("checking if user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);
        userRepository.isOwnerOrCollaboratorPlaylist(userId, playlistId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user do not have access to the playlist_id=%s".formatted(playlistId));
            log.atError()
                    .setCause(e)
    
    
    
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()



                .log("checked user with user_id={} have access to playlist with playlist_id={}", userId, playlistId);


        log.atInfo()



                .log("deleting playlist from playlist_and_song with playlist_id={}", playlistId);
        playlistAndSongRepository.deletePlaylistAndSongEntityByPlaylistId(playlistId);
        log.atInfo()



                .log("deleted playlist from playlists_and_songs with playlist_id={}", playlistId);

        log.atInfo()



                .log("deleting playlist from playlists with playlist_id={}", playlistId);
        playlistRepository.deleteById(playlistId);
        log.atInfo()



                .log("deleted playlist from playlists with playlist_id={}", playlistId);

        return true;
    }
}


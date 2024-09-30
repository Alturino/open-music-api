package com.onirutla.open_music_api.collaboration;

import com.onirutla.open_music_api.core.exception.ForbiddenException;
import com.onirutla.open_music_api.core.exception.NotFoundException;
import com.onirutla.open_music_api.core.exception.UnauthorizedRequestException;
import com.onirutla.open_music_api.playlist.PlaylistEntity;
import com.onirutla.open_music_api.playlist.PlaylistRepository;
import com.onirutla.open_music_api.user.UserEntity;
import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    @Transactional
    public CollaborationEntity createCollaboration(CollaborationRequest request, String ownerUserId) {
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("initiating process create_collaboration for playlist_id={} by user_id={} to user_id={}", request.playlistId(), request.userId(), ownerUserId);

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("find user_id={}", ownerUserId);
        UserEntity ownerUser = userRepository.findById(ownerUserId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(ownerUserId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "create_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("owner_user", ownerUser)
                .log("found user_id={}", ownerUserId);

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("find user_id={}", request.userId());
        UserEntity collaboratorUser = userRepository.findById(request.userId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("user_id=%s not found".formatted(request.userId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "create_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("collaborator_user", collaboratorUser)
                .log("found user_id={}", request.userId());

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("find playlist_id={}", request.playlistId());
        PlaylistEntity playlist = playlistRepository.findById(request.playlistId()).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("playlist_id=%s not found".formatted(request.playlistId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "create_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("playlist", playlist)
                .addKeyValue("collaborator_user", collaboratorUser)
                .log("found playlist_id={}", request.playlistId());

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("playlist", playlist)
                .addKeyValue("collaborator_user", collaboratorUser)
                .log("checking if user_id={} is the owner of playlist_id={}", ownerUserId, request.playlistId());
        playlistRepository.findByIdAndOwnerId(request.playlistId(), ownerUserId).orElseThrow(() -> {
            ForbiddenException e = new ForbiddenException("user_id=%s is not the owner of playlist_id=%s".formatted(ownerUserId, request.playlistId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "create_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("playlist", playlist)
                .addKeyValue("collaborator_user", collaboratorUser)
                .log("checked user_id={} is the owner of playlist_id={}", ownerUserId, request.playlistId());

        CollaborationEntity collaboration = CollaborationEntity.builder()
                .collaboratorId(request.userId())
                .ownerId(ownerUserId)
                .playlistId(request.playlistId())
                .build();
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("playlist", playlist)
                .addKeyValue("collaborator_user", collaboratorUser)
                .addKeyValue("collaboration", collaboration)
                .log("inserting collaboration={}", collaboration);
        CollaborationEntity insertedCollaboration = collaborationRepository.save(collaboration);
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("playlist", playlist)
                .addKeyValue("collaborator_user", collaboratorUser)
                .addKeyValue("collaboration", collaboration)
                .addKeyValue("inserted_collaboration", insertedCollaboration)
                .log("inserted collaboration={}", insertedCollaboration);

        return insertedCollaboration;
    }

    @Transactional
    public CollaborationEntity deleteCollaboration(CollaborationRequest request, String ownerUserId) {
        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("initiating process delete_collaboration for playlist_id={} by user_id={} to user_id={}", request.playlistId(), request.userId(), ownerUserId);

        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("find user_id={}", ownerUserId);
        UserEntity ownerUser = userRepository.findById(ownerUserId).orElseThrow(() -> {
            UnauthorizedRequestException e = new UnauthorizedRequestException("user_id=%s not found".formatted(ownerUserId));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("owner_user", ownerUser)
                .log("found user_id={}", ownerUserId);

        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("checking if user_id={} is the owner of playlist_id={}", ownerUserId, request.playlistId());
        playlistRepository.findByIdAndOwnerId(request.playlistId(), ownerUserId).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("user_id=%s is not the owner of playlist_id=%s".formatted(ownerUserId, request.playlistId()));
            log.atError()
                    .setCause(e)
                    .addKeyValue("process", "delete_collaboration")
                    .addKeyValue("request", request)
                    .addKeyValue("owner_user_id", ownerUserId)
                    .addKeyValue("collaborator_user_id", request.userId())
                    .addKeyValue("playlist_id", request.playlistId())
                    .log(e.getMessage());
            return e;
        });
        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("checked user_id={} is the owner of playlist_id={}", ownerUserId, request.playlistId());

        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("deleting collaborations with owner_user_id={} playlist_id={} collaborator_user_id={}", ownerUserId, request.playlistId(), request.userId());
        collaborationRepository.deleteByOwnerIdAndPlaylistIdAndCollaboratorId(ownerUserId, request.playlistId(), request.userId());
        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("owner_user_id", ownerUserId)
                .addKeyValue("collaborator_user_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("deleted collaborations with owner_user_id={} playlist_id={} collaborator_user_id={}", ownerUserId, request.playlistId(), request.userId());

        return null;
    }

}

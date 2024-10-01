package com.onirutla.open_music_api.collaboration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/collaborations")
@RequiredArgsConstructor
@RestController
@Slf4j
public class CollaborationController {

    private final CollaborationService collaborationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCollaboration(@RequestBody CollaborationRequest request) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("received request={} to create collaboration for playlist_id={} requester_user_id={} collaborator_id={}", request, request.playlistId(), userId, request.userId());

        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("inserting collaboration={} for playlist_id={} requester_user_id={} collaborator_id={}", request, request.playlistId(), userId, request.userId());
        CollaborationEntity insertedCollaboration = collaborationService.createCollaboration(request, userId);
        log.atInfo()
                .addKeyValue("process", "create_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", userId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("inserted collaboration={} for playlist_id={} by requester_user_id={} to collaborator_id={}", insertedCollaboration, request.playlistId(), userId, request.userId());

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("collaborationId", insertedCollaboration.getId())
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("data", data)
                .put("status", "success")
                .put("message", "inserted collaboration=%s for playlist_id=%s by requester_user_id=%s to collaborator_id=%s".formatted(insertedCollaboration, request.playlistId(), userId, request.userId()))
                .get();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteCollaboration(@RequestBody CollaborationRequest request) {
        String requesterUserId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("received request={} to delete collaboration for playlist_id={} requester_user_id={} collaborator_id={}", request, request.playlistId(), requesterUserId, request.userId());

        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .log("deleting request={} to delete collaboration for playlist_id={} requester_user_id={} collaborator_id={}", request, request.playlistId(), requesterUserId, request.userId());
        CollaborationEntity deletedCollaboration = collaborationService.deleteCollaboration(request, requesterUserId);
        log.atInfo()
                .addKeyValue("process", "delete_collaboration")
                .addKeyValue("request", request)
                .addKeyValue("requester_user_id", requesterUserId)
                .addKeyValue("collaborator_id", request.userId())
                .addKeyValue("playlist_id", request.playlistId())
                .addKeyValue("deleted_collaboration", deletedCollaboration)
                .log("deleted request={} to delete collaboration for playlist_id={} requester_user_id={} collaborator_id={}", request, request.playlistId(), requesterUserId, request.userId());

        Map<String, Object> data = new StringObjectMapBuilder()
                .put("collaborationId", deletedCollaboration.getId())
                .get();
        Map<String, Object> body = new StringObjectMapBuilder()
                .put("data", data)
                .get();
        return ResponseEntity.ok(body);
    }
}

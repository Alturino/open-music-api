package com.onirutla.open_music_api.collaboration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(null);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteCollaboration(@RequestBody CollaborationRequest request) {
        return ResponseEntity.ok(null);
    }
}

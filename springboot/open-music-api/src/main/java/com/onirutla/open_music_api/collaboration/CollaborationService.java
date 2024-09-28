package com.onirutla.open_music_api.collaboration;

import com.onirutla.open_music_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CollaborationService {
    private final CollaborationRepository collaborationRepository;
    private final UserRepository userRepository;

    public CollaborationEntity createCollaboration(CollaborationRequest request) {
        return null;
    }

    public CollaborationEntity deleteCollaboration(CollaborationRequest request) {
        return null;
    }
}

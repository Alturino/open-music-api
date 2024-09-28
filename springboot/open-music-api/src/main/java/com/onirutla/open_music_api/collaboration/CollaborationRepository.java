package com.onirutla.open_music_api.collaboration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollaborationRepository extends JpaRepository<CollaborationEntity, String> {
}

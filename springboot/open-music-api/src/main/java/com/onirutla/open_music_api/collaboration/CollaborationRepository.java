package com.onirutla.open_music_api.collaboration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaborationRepository extends JpaRepository<CollaborationEntity, String> {
    @Modifying
    @Query(value = "delete from collaborations as c where c.owner_id = :ownerId and c.playlist_id = :playlistId and c.collaborator_id = :collaboratorId returning c", nativeQuery = true)
    List<CollaborationEntity> deleteByOwnerIdAndPlaylistIdAndCollaboratorId(String ownerId, String playlistId, String collaboratorId);

    List<CollaborationEntity> findCollaborationsByOwnerIdAndPlaylistIdAndCollaboratorId(String ownerId, String playlistId, String collaboratorId);
}

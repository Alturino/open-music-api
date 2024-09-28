package com.onirutla.open_music_api.collaboration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity(name = "collaborations")
@NoArgsConstructor
@Table(name = "collaborations")
public class CollaborationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "collaborator_id", nullable = false)
    private String collaboratorId;

    @Column(name = "playlist_id", nullable = false)
    private String playlistId;
}

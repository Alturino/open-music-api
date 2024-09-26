package com.onirutla.open_music_api.playlist.activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
@Entity(name = "playlist_activities")
@NoArgsConstructor
@Table(name = "playlist_activities")
public class PlaylistActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "playlist_id")
    private String playlistId;

    @Column(name = "song_id")
    private String songId;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(value = EnumType.STRING)
    private PlaylistActivityAction playlistActivityAction;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}

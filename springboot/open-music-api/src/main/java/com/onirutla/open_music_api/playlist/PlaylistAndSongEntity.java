package com.onirutla.open_music_api.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
@Entity(name = "playlists_and_songs")
@NoArgsConstructor
@Table(name = "playlists_and_songs")
public class PlaylistAndSongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "playlist_id", nullable = false)
    private String playlistId;

    @Column(name = "song_id", nullable = false)
    private String songId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @JsonIgnore
    private Timestamp updatedAt;
}

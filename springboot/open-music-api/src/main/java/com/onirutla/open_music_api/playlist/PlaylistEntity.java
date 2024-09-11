package com.onirutla.open_music_api.playlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Entity(name = "playlists")
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@Setter
@Table(name = "playlists")
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String performer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private int year;

    @CreationTimestamp
    @JsonIgnore
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @JsonIgnore
    @Column(nullable = false)
    private Timestamp updatedAt;

    @Override
    public String toString() {
        return "%s(id = '%s', ownerId = '%s', genre = '%s', performer = '%s', title = '%s', duration = %d, year = %d, createdAt = %s, updatedAt = %s)".formatted(
                getClass().getSimpleName(),
                id,
                ownerId,
                genre,
                performer,
                title,
                duration,
                year,
                createdAt,
                updatedAt
        );
    }
}

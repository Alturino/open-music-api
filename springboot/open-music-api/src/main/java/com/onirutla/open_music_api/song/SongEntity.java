package com.onirutla.open_music_api.song;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.onirutla.open_music_api.album.AlbumEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Entity
@Getter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@NoArgsConstructor
@Setter
public class SongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String performer;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int duration;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @JsonIgnore
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AlbumEntity album;

    @Override
    public String toString() {
        return "%s(id = %s, title = %s, year = %d, performer = %s, genre = %s, duration = %d, createdAt = %s, updatedAt = %s)"
                .formatted(getClass().getSimpleName(), id, title, year, performer, genre, duration, createdAt, updatedAt);
    }
}
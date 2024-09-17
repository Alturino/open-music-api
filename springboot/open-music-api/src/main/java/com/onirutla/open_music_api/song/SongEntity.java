package com.onirutla.open_music_api.song;

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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
@Entity(name = "songs")
@NoArgsConstructor
@Table(name = "songs")
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

    @Column(name = "album_id")
    private String albumId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    @JsonIgnore
    private Timestamp updatedAt;
}

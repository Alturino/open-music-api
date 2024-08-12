package com.onirutla.open_music_api.album;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.onirutla.open_music_api.song.SongEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Builder
@Entity(name = "albums")
@Getter
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@NoArgsConstructor
@Setter
@Table(name = "albums")
public class AlbumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int year;

    @CreationTimestamp
    @JsonIgnore
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @JsonIgnore
    @Column(updatable = false)
    private Timestamp updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private List<SongEntity> songs;

    @Override
    public String toString() {
        return "%s(id = %s, name = %s, year = %d, createdAt = %s, updatedAt = %s)"
                .formatted(getClass().getSimpleName(), id, name, year, createdAt, updatedAt);
    }
}

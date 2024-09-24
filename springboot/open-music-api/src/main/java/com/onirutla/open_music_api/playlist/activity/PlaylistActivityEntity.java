package com.onirutla.open_music_api.playlist.activity;

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
@Entity(name = "playlist_activities")
@NoArgsConstructor
@Table(name = "playlist_activities")
public class PlaylistActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}

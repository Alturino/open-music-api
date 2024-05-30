create table if not exists playlists_and_songs (
    id uuid primary key,
    playlist_id uuid not null,
    constraint fk_playlist foreign key (playlist_id)
    references playlists (id)
    on delete cascade
    song_id uuid not null,
    constraint fk_song foreign key (song_id)
    references songs (song_id)
    on delete cascade
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    deleted_at timestamp with time zone,
);

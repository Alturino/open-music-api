create table if not exists playlists (
    id uuid primary key,
    name text not null,
    owner_id uuid not null,
    constraint fk_owner foreign key (owner_id)
    references users (id)
    on delete cascade
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    deleted_at timestamp with time zone,
);

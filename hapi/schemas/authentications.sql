create table if not exists authentications (
    id uuid primary key,
    token text,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    deleted_at timestamp with time zone,
);

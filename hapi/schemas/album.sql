create table if not exists album (
    id uuid primary key,
    name varchar(50) not null,
    year int not null,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone not null default now(),
    deleted_at timestamp with time zone,
);

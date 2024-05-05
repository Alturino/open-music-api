create table if not exists song (
    id uuid primary key,
    title varchar(50) not null,
    year int not null,
    genre varchar(20) not null,
    performer varchar(50) not null,
    duration interval not null,
    album_id uuid not null,
    constraint fk_album foreign key (album_id)
    references album (id)
    on delete cascade
);

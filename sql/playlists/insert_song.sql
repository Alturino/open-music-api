insert into playlists (id, name, owner_id) values ($1, $2, $3) returning id;

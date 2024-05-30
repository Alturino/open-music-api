select u.id from users as u inner join playlists as p on u.id = p.owner_id;

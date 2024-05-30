select
    p.id,
    p.name,
    p.owner_id,
    s.id,
    s.title,
    s.year,
    s.genre,
    s.performer,
    s.duration,
    s.album_id
from playlists as p
left join users as u on p.owner_id = u.id
left join playlists_and_songs as ps on p.id = ps.playlist_id
left join songs as s on ps.song_id = s.id
where p.owner_id = $1;

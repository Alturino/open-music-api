select
    p.id,
    p.name,
    u.username
from playlists as p
inner join users as u on p.owner_id = u.id
inner join collaborations as c on p.id = c.playlist_id
where p.owner_id = $1 or c.user_id = $1;

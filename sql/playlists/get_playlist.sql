select p.id
from playlists as p
inner join users as u on p.owner_id = u.id
inner join collaborations as c on p.id = c.playlist_id
where p.id = $1 (u.id = $2 or c.user_id = $2);

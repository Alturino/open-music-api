select p.id
from playlists as p
left join collaborations as c on p.id = c.playlist_id
where p.id = $1 and (p.owner_id = $2 or c.user_id = $2);

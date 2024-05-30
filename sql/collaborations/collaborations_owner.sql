select u.id
from users as u
left join playlists as p on u.id = p.owner_id
left join collaborations as c on p.id = c.playlist_id
where p.id = $1 and owner_id = $2

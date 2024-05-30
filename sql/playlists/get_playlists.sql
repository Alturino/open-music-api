select
    p.id,
    p.name,
    u.username
from playlists as p
left join users as u on p.owner_id = u.id
where u.id = $1;

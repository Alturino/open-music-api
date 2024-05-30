select
    u.username,
    s.title,
    pa.action,
    pa.time
from playlists as p
inner join playlist_activities as pa on p.id = pa.playlist_id
inner join songs as s on pa.song_id = s.id
inner join users as u on p.owner_id = u.id
left join collaborations as c on p.id = c.playlist_id
where
    p.id = '7eqcsUIr-3sZbBoR' and (c.user_id = 'vw08YjkdMxUOfSK9' or p.owner_id = 'vw08YjkdMxUOfSK9');

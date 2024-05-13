select
    a.name,
    a.year
from albums as a
inner join songs as s on a.id = s.album_id
where a.id = $1;

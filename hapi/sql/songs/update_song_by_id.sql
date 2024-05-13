update songs set
    title = $2,
    year = $3,
    genre = $4,
    performer = $5,
    duration = $6,
    album_id = $7
where id = $1 returning id,
title,
year,
genre,
performer,
duration,
album_id;

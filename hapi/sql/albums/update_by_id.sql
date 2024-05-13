update albums set name = $2, year = $3 where id = $1 returning id, name, year;

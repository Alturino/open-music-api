const { nanoid } = require('nanoid');
const InvariantError = require('../exceptions/InvariantError');

class SongService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addSong({ title, year, genre, performer, duration, albumId }) {
    const id = nanoid(16);
    console.log(
      `SongService addSong title: ${title}, year: ${year}, genre: ${genre}, performer: ${performer}, duration: ${duration}, albumId: ${albumId}`,
    );
    const query = {
      text: 'insert into songs(id, title, year, genre, performer, duration, album_id) values($1, $2, $3, $4, $5, $6, $7) returning id;',
      values: [id, title, year, genre, performer, duration, albumId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('song gagal ditambahkan');
      console.error(`SongService addSong ${e}`);
      throw e;
    }
    return result.rows[0].id;
  }

  async updateSong({ id, title, year, genre, performer, duration, albumId }) {
    console.log(
      `SongService updateSong title: ${title}, year: ${year}, genre: ${genre}, performer: ${performer}, duration: ${duration}, albumId: ${albumId}`,
    );
    const query = {
      text: 'update songs set title = $2, year = $3, genre = $4, performer = $5, duration = $6, album_id = $7 where id = $1 returning id, title, year, genre, performer, duration, album_id;',
      values: [id, title, year, genre, performer, duration, albumId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('song gagal diupdate', 404);
      console.error(`SongService updateSong ${e}`);
      throw e;
    }
    return result.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        year: row.year,
        genre: row.genre,
        performer: row.performer,
        duration: row.duration,
        albumId: row.albumId,
      };
    })[0];
  }

  async getSongById({ id }) {
    const query = {
      text: 'select id, title, year, genre, performer, duration from songs where id = $1;',
      values: [id],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError(`song dengan id: ${id} tidak ditemukan`, 404);
      console.error(`SongService getSongById ${e}`);
      throw e;
    }
    return result.rows.map((row) => {
      console.log(`SongService getSongById ${JSON.stringify(row)}`);
      const song = {
        id: row.id,
        title: row.title,
        year: row.year,
        genre: row.genre,
        performer: row.performer,
        duration: row.duration,
      };
      return song;
    })[0];
  }

  async getSongs({ title = '', performer = '' }) {
    console.log(`SongService getSongs title: ${title}, performer: ${performer}`);
    const query = {
      text: 'select id, title, performer from songs where title ilike $1 and performer ilike $2;',
      values: [`%${title}%`, `%${performer}%`],
    };
    console.log(`SongService getSongs queryString: ${JSON.stringify(query)}`);

    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError(`Songs kosong`);
      console.error(`SongService getSongs ${e}`);
      throw e;
    }
    return result.rows.map((row) => {
      console.log(`SongService getSongs row: ${JSON.stringify(row)}`);
      return { id: row.id, title: row.title, performer: row.performer };
    });
  }

  async deleteSong({ id }) {
    console.log(`SongService deleteSong id: ${id}`);
    const query = {
      text: 'delete from songs where id = $1 returning id;',
      values: [id],
    };
    const result = await this._pool.query(query);
    console.log(`SongService deleteSong rows: ${JSON.stringify(result.rows)}`);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('song gagal dihapus', 404);
      console.error(`SongService deleteSong ${e}`);
      throw e;
    }
    return result.rows[0].id;
  }
}
module.exports = SongService;

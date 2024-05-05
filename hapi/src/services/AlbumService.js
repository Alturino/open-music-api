const { nanoid } = require('nanoid');
const InvariantError = require('../exceptions/InvariantError');
const NotFoundError = require('../exceptions/NotFoundError');

class AlbumService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addAlbum({ name, year }) {
    const id = nanoid(16);
    console.log(`AlbumService addAlbum name: ${name}, year: ${year}`);
    const query = {
      text: 'insert into albums(id, name, year) values($1, $2, $3) returning id;',
      values: [id, name, year],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('album gagal ditambahkan');
      console.error(e);
      throw e;
    }
    console.log(`AlbumService addAlbum rows: ${JSON.stringify(result.rows)}`);
    return result.rows[0].id;
  }

  async updateAlbum({ id, name, year }) {
    console.log(`AlbumService updateAlbum id: ${id}, name: ${name}, year: ${year}`);
    const query = {
      text: 'update albums set name = $2, year = $3 where id = $1 returning id, name, year;',
      values: [id, name, year],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('album gagal diperbarui', 404);
      console.error(e);
      throw e;
    }
    const albums = result.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        year: row.year,
      };
    });
    console.log(`AlbumService updateAlbum ${albums}`);
    return albums[0];
  }

  async getAlbumById({ id }) {
    console.log(`AlbumService getAlbumById id: ${id}`);
    const queryAlbum = {
      text: 'select id, name, year from albums where id = $1;',
      values: [id],
    };
    const resultAlbum = await this._pool.query(queryAlbum);
    console.log(`AlbumService getAlbumById rowCount: ${JSON.stringify(resultAlbum.rows)}`);
    if (resultAlbum.rowCount === null || resultAlbum.rowCount < 1) {
      const e = new NotFoundError(
        `AlbumService getAlbumById album dengan id: ${id} tidak ditemukan`,
      );
      console.error(e);
      throw e;
    }
    const querySong = {
      text: 'select id, title, year, genre, performer, duration, album_id from songs where album_id = $1;',
      values: [id],
    };
    const resultSongs = await this._pool.query(querySong);
    const songs = resultSongs.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        year: row.year,
        genre: row.genre,
        performer: row.performer,
        duration: row.duration,
        album_id: row.album_id,
      };
    });
    const albums = resultAlbum.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        year: row.year,
        songs: songs,
      };
    });
    console.log(
      `AlbumService getAlbumById albums: ${JSON.stringify(albums)}, songs: ${JSON.stringify(songs)}`,
    );
    return albums[0];
  }

  async getAlbums() {
    const query = {
      text: 'select a.name, a.year from albums as a left join songs as s on a.id = s.album_id;',
      values: [],
    };
    const result = await this._pool.query(query);
    console.log(JSON.stringify(result.rows));
    return result.rows;
  }

  async deleteAlbum({ id }) {
    console.log(`AlbumService deleteAlbum id: ${id}`);
    const query = {
      text: 'delete from albums where id = $1 returning id;',
      values: [id],
    };
    const result = await this._pool.query(query);
    console.log(`AlbumService deleteAlbum rows: ${JSON.stringify(result.rows)}`);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new NotFoundError(`album dengan id: ${id} gagal dihapus`);
      console.error(e);
      throw e;
    }
    return result.rows[0].id;
  }
}

module.exports = AlbumService;

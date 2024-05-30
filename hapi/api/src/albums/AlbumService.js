const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');
const NotFoundError = require('../core/exceptions/NotFoundError');

class AlbumService {
  constructor(pgPool) {
    this._pool = pgPool;

    this.addAlbum = this.addAlbum.bind(this);
    this.updateAlbum = this.updateAlbum.bind(this);
    this.getAlbumById = this.getAlbumById.bind(this);
    this.getAlbums = this.getAlbums.bind(this);
    this.getAlbumById = this.getAlbumById.bind(this);
  }

  async addAlbum(name, year) {
    const id = nanoid(16);
    const query = {
      text: 'insert into albums(id, name, year) values($1, $2, $3);',
      values: [id, name, year],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('album gagal ditambahkan');
      throw e;
    }
    return id;
  }

  async updateAlbum(id, name, year) {
    const query = {
      text: 'update albums set name = $2, year = $3 where id = $1 returning id, name, year;',
      values: [id, name, year],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Album dengan id: ${id} tidak ditemukan`);
      throw e;
    }
    const albums = result.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        year: row.year,
      };
    });
    return albums[0];
  }

  async getAlbumById(id) {
    const queryAlbum = {
      text: 'select id, name, year, cover_url from albums where id = $1;',
      values: [id],
    };
    const resultAlbum = await this._pool.query(queryAlbum);
    if (resultAlbum.rowCount === null || resultAlbum.rowCount < 1) {
      const e = new NotFoundError(
        `AlbumService getAlbumById album dengan id: ${id} tidak ditemukan`,
      );
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
        coverUrl: row.cover_url,
        songs: songs,
      };
    });
    return albums[0];
  }

  async getAlbums() {
    const query = {
      text: 'select a.name, a.year from albums as a left join songs as s on a.id = s.album_id;',
      values: [],
    };
    const result = await this._pool.query(query);
    return result.rows;
  }

  async deleteAlbum(id) {
    const query = {
      text: 'delete from albums where id = $1;',
      values: [id],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`album dengan id: ${id} gagal dihapus`);
      throw e;
    }
  }

  async addCoverToAlbum(albumId, coverUrl) {
    const query = {
      text: `
        update albums set cover_url = $2 where id = $1;
        `,
      values: [albumId, coverUrl],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`album dengan id=${albumId} gagal dihapus`);
      throw e;
    }
  }
}

module.exports = AlbumService;

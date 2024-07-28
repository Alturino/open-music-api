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
    this.addCoverToAlbum = this.addCoverToAlbum.bind(this);
    this.addAlbumLike = this.addAlbumLike.bind(this);
    this.unlikeAlbum = this.unlikeAlbum.bind(this);
    this.getAlbumLike = this.getAlbumLike.bind(this);
  }

  async addAlbum(name, year) {
    const id = nanoid(16);
    const query = {
      text: 'insert into albums(id, name, year) values($1, $2, $3);',
      values: [id, name, year],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      throw new InvariantError('album gagal ditambahkan');
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
      throw new NotFoundError(`Album dengan id: ${id} tidak ditemukan`);
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
      throw new NotFoundError(`AlbumService getAlbumById album dengan id: ${id} tidak ditemukan`);
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
      throw new NotFoundError(`album dengan id: ${id} gagal dihapus`);
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
      throw new NotFoundError(`album dengan id=${albumId} gagal dihapus`);
    }
  }

  async addAlbumLike(req, userId, albumId) {
    const client = await this._pool.connect();
    const userAlbumLikesId = nanoid(16);
    try {
      await client.query('begin');

      const albumQuery = {
        text: 'select * from albums where id = $1',
        values: [albumId],
      };
      const albumResult = await client.query(albumQuery);
      if (albumResult.rowCount === null || albumResult.rowCount === 0) {
        throw new NotFoundError(`album dengan id=${albumId} tidak ditemukan`);
      }

      const userAlbumLikesQuery = {
        text: `select id, user_id, album_id from user_album_likes where user_id = $1 and album_id = $2;`,
        values: [userId, albumId],
      };
      const userAlbumLikesQueryResult = await client.query(userAlbumLikesQuery);
      if (userAlbumLikesQueryResult.rowCount === null || userAlbumLikesQueryResult.rowCount === 0) {
        const insertUserAlbumLikesQuery = {
          text: `insert into user_album_likes(id, user_id, album_id) values ($1, $2, $3);`,
          values: [userAlbumLikesId, userId, albumId],
        };
        const insertUserAlbumLikesQueryResult = await client.query(insertUserAlbumLikesQuery);
        if (
          insertUserAlbumLikesQueryResult.rowCount === null ||
          insertUserAlbumLikesQueryResult.rowCount === 0
        ) {
          throw new InvariantError('Failed to insert into user_album_likes');
        }

        const getAlbumLikeCountQuery = {
          text: 'select like_count from albums where id = $1',
          values: [albumId],
        };
        const getAlbumLikeCountQueryResult = await client.query(getAlbumLikeCountQuery);
        req.log(
          ['INF', 'AlbumService', 'addAlbumLike'],
          `getAlbumLikeCountQueryResult=${JSON.stringify(getAlbumLikeCountQueryResult)}`,
        );
        if (
          getAlbumLikeCountQueryResult.rowCount === null ||
          getAlbumLikeCountQueryResult.rowCount === 0
        ) {
          throw new InvariantError('Failed to get like_count');
        }
        const albumLikeCount =
          getAlbumLikeCountQueryResult.rows[0].like_count === null
            ? 0
            : getAlbumLikeCountQueryResult.rows[0].like_count;
        req.log(['INF', 'AlbumService', 'addAlbumLike'], `albumLikeCount=${albumLikeCount}`);

        const increaseAlbumLikeCountQuery = {
          text: 'update albums set like_count = $1 + 1 where id = $2;',
          values: [albumLikeCount, albumId],
        };
        const increaseAlbumLikeCountQueryResult = await client.query(increaseAlbumLikeCountQuery);
        if (
          increaseAlbumLikeCountQueryResult.rowCount === null ||
          increaseAlbumLikeCountQueryResult.rowCount === 0
        ) {
          throw new InvariantError('Failed to update album like_count');
        }
        await client.query('COMMIT');
        return;
      }
      throw new InvariantError(
        `Failed to like albumId=${albumId} because user already like this album`,
      );
    } catch (e) {
      await client.query('ROLLBACK');
      throw e;
    } finally {
      client.release();
    }
  }

  async getAlbumLike(req, albumId) {
    const query = {
      text: 'select like_count from albums where id = $1',
      values: [albumId],
    };
    req.log(
      ['INF', 'AlbumService', 'getAlbumLike'],
      `Starting the query to fetch like_count from album_id=${albumId}`,
    );
    const queryResult = await this._pool.query(query);
    const likeCount = queryResult.rows[0].like_count;
    req.log(
      ['INF', 'AlbumService', 'getAlbumLike'],
      `Finished the query to fetch like_count from album_id=${albumId} with like_count=${likeCount}`,
    );
    return likeCount;
  }

  async unlikeAlbum(req, userId, albumId) {
    const client = await this._pool.connect();
    try {
      await client.query('BEGIN');

      const deleteUserAlbumLikesQuery = {
        text: 'delete from user_album_likes where user_id = $1 and album_id = $2;',
        values: [userId, albumId],
      };
      const deleteUserAlbumLikesQueryResult = await client.query(deleteUserAlbumLikesQuery);
      if (
        deleteUserAlbumLikesQueryResult.rowCount === null ||
        deleteUserAlbumLikesQueryResult.rowCount === 0
      ) {
        throw new NotFoundError(
          `user_album_likes dengan user_id=${userId} dan album_id=${albumId} tidak ditemukan`,
        );
      }

      const getLikeCountQuery = {
        text: 'select like_count from albums where id = $1',
        values: [albumId],
      };
      const getLikeCountQueryResult = await client.query(getLikeCountQuery);
      if (getLikeCountQueryResult.rowCount === null || getLikeCountQueryResult.rowCount === 0) {
        throw new NotFoundError(`album dengan id=${albumId} tidak ditemukan`);
      }

      const albumLikeCount = getLikeCountQueryResult.rows[0].like_count;
      const updateAlbumLikeCountQuery = {
        text: 'update albums set like_count = $1 - 1 where id = $2',
        values: [albumLikeCount, albumId],
      };
      const updateAlbumLikeCountQueryResult = await client.query(updateAlbumLikeCountQuery);
      if (
        updateAlbumLikeCountQueryResult.rowCount === null ||
        updateAlbumLikeCountQueryResult.rowCount === 0
      ) {
        throw new NotFoundError(`Gagal mengupdate album like_count`);
      }

      await client.query('COMMIT');
    } catch (e) {
      await client.query('ROLLBACK');
      throw e;
    } finally {
      client.release();
    }
  }
}

module.exports = AlbumService;

const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');
const NotFoundError = require('../core/exceptions/NotFoundError');
const AuthorizationError = require('../core/exceptions/AuthorizationError');

class PlaylistService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addPlaylist(name, ownerId) {
    const id = nanoid(16);
    console.log(`PlaylistService addPlaylist name: ${name}, ownerId: ${ownerId}`);
    const query = {
      text: 'insert into playlists(id, name, owner_id) values($1, $2, $3) returning id;',
      values: [id, name, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('playlist gagal ditambahkan');
      console.error(`PlaylistService addPlaylist ${e}`);
      throw e;
    }
    console.log(
      `PlaylistService addPlaylist name: ${name}, ownerId: ${ownerId} result: ${JSON.stringify(result.rows)}`,
    );
    return result.rows[0].id;
  }

  async getPlaylists(ownerId) {
    console.log(`PlaylistService getPlaylists ownerId: ${ownerId}`);
    const playlistQuery = {
      text: `
        select
            p.id,
            p.name,
            u.username
        from playlists as p
        left join users as u on p.owner_id = u.id
        where u.id = $1;`,
      values: [ownerId],
    };
    const playlistQueryResult = await this._pool.query(playlistQuery);
    if (playlistQueryResult.rowCount === null || playlistQueryResult.rowCount < 1) {
      const e = new InvariantError('playlist tidak ditemukan');
      console.error(`PlaylistService getPlaylists ${e}`);
      throw e;
    }
    console.log(
      `PlaylistService getPlaylists ownerId: ${ownerId}, result: ${JSON.stringify(playlistQueryResult.rows)}`,
    );
    const playlists = playlistQueryResult.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        username: row.username,
      };
    });
    console.log(`PlaylistService getPlaylists playlistsAndSongs: ${JSON.stringify(playlists)}`);
    return playlists;
  }

  async deletePlaylist(playlistId, ownerId) {
    await this.verifyPlaylistOwner(playlistId, ownerId);
    console.log(`PlaylistService deletePlaylist id: ${playlistId}, ownerId: ${ownerId}`);
    const query = {
      text: 'delete from playlists where id = $1 and owner_id = $2;',
      values: [playlistId, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('playlist gagal dihapus');
      console.error(`PlaylistService deletePlaylist ${e}`);
      throw e;
    }
  }

  async addSongToPlaylist(playlistId, songId, ownerId) {
    await this.verifyPlaylistOwner(playlistId, ownerId);
    console.log(
      `PlaylistService addSongToPlaylist playlistId: ${playlistId}, songId: ${songId}, ownerId: ${ownerId}`,
    );
    const queryIsSongExist = {
      text: 'select id from songs where id = $1;',
      values: [songId],
    };
    const resultQueryIsSongExist = await this._pool.query(queryIsSongExist);
    if (resultQueryIsSongExist.rowCount < 1) {
      const e = new NotFoundError('playlist tidak ditemukan');
      console.error(`PlaylistService addSongToPlaylist ${e}`);
      throw e;
    }

    const query = {
      text: 'insert into playlists_and_songs(playlist_id, song_id) values($1, $2);',
      values: [playlistId, songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('playlist gagal dihapus');
      console.error(`PlaylistService addSongToPlaylist ${e}`);
      throw e;
    }
  }

  async getSongsInPlaylist(playlistId, ownerId) {
    console.log(
      `PlaylistService getSongsInPlaylist playlistId: ${playlistId}, ownerId: ${ownerId}`,
    );
    await this.verifyPlaylistExist(playlistId);
    await this.verifyPlaylistOwner(playlistId, ownerId);
    const queryPlaylist = {
      text: `
        select
            p.id,
            p.name,
            u.username
        from playlists as p
        left join users as u on p.owner_id = u.id
        where p.id = $1;`,
      values: [playlistId],
    };
    const queryPlaylistResult = await this._pool.query(queryPlaylist);
    const playlist = queryPlaylistResult.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        username: row.username,
      };
    })[0];
    const querySongs = {
      text: `select
              s.id,
              s.title,
              s.year,
              s.genre,
              s.performer,
              s.duration,
              s.album_id
          from playlists as p
          inner join users as u on p.owner_id = u.id
          inner join playlists_and_songs as ps on p.id = ps.playlist_id
          inner join songs as s on ps.song_id = s.id
          where ps.playlist_id = $1 and p.owner_id = $2;`,
      values: [playlistId, ownerId],
    };
    const querySongsResult = await this._pool.query(querySongs);
    if (querySongsResult.rowCount === null || querySongsResult.rowCount < 1) {
      const e = new InvariantError('song tidak ditemukan');
      console.error(`PlaylistService getSongsInPlaylist ${e}`);
      throw e;
    }
    const songs = querySongsResult.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        performer: row.performer,
      };
    });
    return {
      ...playlist,
      songs,
    };
  }

  async deleteSongInPlaylist(playlistId, ownerId, songId) {
    await this.verifyPlaylistOwner(playlistId, ownerId);
    console.log(
      `PlaylistService deleteSongInPlaylist id: ${playlistId}, ownerId: ${ownerId}, songId: ${songId}`,
    );
    const query = {
      text: 'delete from playlists_and_songs as ps where ps.playlist_id = $1 and ps.song_id = $2 returning ps.playlist_id, ps.song_id;',
      values: [playlistId, songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('Playlist gagal dihapus');
      console.error(`PlaylistService getSongsInPlaylist ${e}`);
      throw e;
    }
    return result.rows.map((row) => {
      return {
        songId: row.song_id,
        playlistId: row.playlist_id,
      };
    });
  }

  async verifyPlaylistOwner(playlistId, ownerId) {
    console.log(
      `PlaylistService verifyPlaylistOwner playlistId: ${playlistId}, ownerId: ${ownerId}`,
    );
    const query = {
      text: 'select from playlists where id = $1 and owner_id = $2;',
      values: [playlistId, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new AuthorizationError(
        `User dengan ownerId: ${ownerId} tidak berhak mengakses playlist dengan playlistId: ${playlistId}`,
      );
      console.error(`PlaylistService verifyPlaylistOwner ${e}`);
      throw e;
    }
    console.log(
      `PlaylistService verifyPlaylistOwner berhak mengakses playlist dengan playlistId: ${playlistId}`,
    );
  }

  async verifyPlaylistExist(playlistId) {
    console.log(`PlaylistService isPlaylistExists playlistId: ${playlistId}`);
    const query = {
      text: 'select from playlists where id = $1;',
      values: [playlistId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new NotFoundError(`Playlist dengan playlistId: ${playlistId} tidak ditemukan`);
      console.error(`PlaylistService isPlaylistExists ${e}`);
      throw e;
    }
    console.log('PlaylistService isPlaylistExists: true');
  }
}
module.exports = PlaylistService;

const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');
const NotFoundError = require('../core/exceptions/NotFoundError');
const AuthorizationError = require('../core/exceptions/AuthorizationError');

class PlaylistService {
  constructor(pgPool) {
    this._pool = pgPool;

    this.addPlaylist = this.addPlaylist.bind(this);
    this.getPlaylists = this.getPlaylists.bind(this);
    this.deletePlaylist = this.deletePlaylist.bind(this);
    this.addSongToPlaylist = this.addSongToPlaylist.bind(this);
    this.getSongsInPlaylist = this.getSongsInPlaylist.bind(this);
    this.deleteSongInPlaylist = this.deleteSongInPlaylist.bind(this);
    this.getActivities = this.getActivities.bind(this);
    this.isPlaylistExist = this.isPlaylistExist.bind(this);
    this.isSongExist = this.isSongExist.bind(this);
    this.isPlaylistOwnerOrCollaborator = this.isPlaylistOwnerOrCollaborator.bind(this);
    this.isHavePlaylistWriteAccess = this.isHavePlaylistWriteAccess.bind(this);
    this.addToActivity = this.addToActivity.bind(this);
  }

  async addPlaylist(name, ownerId) {
    const id = nanoid(16);
    const query = {
      text: 'insert into playlists(id, name, owner_id) values($1, $2, $3) returning id;',
      values: [id, name, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('playlist gagal ditambahkan');
      throw e;
    }
    return result.rows[0].id;
  }

  async getPlaylists(ownerId) {
    const query = {
      text: `
        select
            p.id,
            p.name,
            u.username
        from playlists as p
        left join users as u on p.owner_id = u.id
        left join collaborations as c on p.id = c.playlist_id
        where p.owner_id = $1 or c.user_id = $1;`,
      values: [ownerId],
    };
    const result = await this._pool.query(query);
    return result.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        username: row.username,
      };
    });
  }

  async deletePlaylist(playlistId, ownerId) {
    await this.isPlaylistExist(playlistId);
    await this.isHavePlaylistWriteAccess(playlistId, ownerId);
    const query = {
      text: 'delete from playlists where id = $1;',
      values: [playlistId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Playlist dengan id: ${playlistId} tidak ditemukan`);
      throw e;
    }
  }

  async addSongToPlaylist(playlistId, songId, ownerId) {
    await this.isPlaylistExist(playlistId);
    await this.isSongExist(songId);
    await this.isPlaylistOwnerOrCollaborator(playlistId, ownerId);
    const query = {
      text: 'insert into playlists_and_songs(playlist_id, song_id) values($1, $2);',
      values: [playlistId, songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('playlist gagal dihapus');
      throw e;
    }
    await this.addToActivity(playlistId, ownerId, 'add', songId);
  }

  async getSongsInPlaylist(playlistId, ownerId) {
    await this.isPlaylistExist(playlistId);
    await this.isPlaylistOwnerOrCollaborator(playlistId, ownerId);
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
          left join users as u on p.owner_id = u.id
          left join playlists_and_songs as ps on p.id = ps.playlist_id
          left join songs as s on ps.song_id = s.id
          left join collaborations as c on p.id = c.playlist_id
          where ps.playlist_id = $1 and (p.owner_id = $2 or c.user_id = $2);`,
      values: [playlistId, ownerId],
    };
    const querySongsResult = await this._pool.query(querySongs);
    if (querySongsResult.rowCount === null || querySongsResult.rowCount === 0) {
      const e = new InvariantError('song tidak ditemukan');
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
    await this.isPlaylistExist(playlistId);
    await this.isSongExist(songId);
    await this.isPlaylistOwnerOrCollaborator(playlistId, ownerId);
    const query = {
      text: 'delete from playlists_and_songs as ps where ps.playlist_id = $1 and ps.song_id = $2 returning ps.playlist_id, ps.song_id;',
      values: [playlistId, songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('Playlist gagal dihapus');
      throw e;
    }
    await this.addToActivity(playlistId, ownerId, 'delete', songId);
    return result.rows.map((row) => {
      return {
        songId: row.song_id,
        playlistId: row.playlist_id,
      };
    });
  }

  async getActivities(playlistId, userId) {
    await this.isPlaylistExist(playlistId);
    await this.isPlaylistOwnerOrCollaborator(playlistId, userId);
    const query = {
      text: `
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
            p.id = $1 and (c.user_id = $2 or p.owner_id = $2);`,
      values: [playlistId, userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthorizationError(
        `User dengan id: ${userId} tidak berhak mengakses activities dari playlist dengan id: ${playlistId}`,
      );
      throw e;
    }
    return result.rows.map((row) => {
      return {
        username: row.username,
        title: row.title,
        action: row.action,
        time: row.time,
      };
    });
  }

  async isPlaylistExist(playlistId) {
    const query = {
      text: 'select id from playlists where id = $1;',
      values: [playlistId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Playlist dengan id: ${playlistId} tidak ditemukan`);
      throw e;
    }
  }

  async isSongExist(songId) {
    const query = {
      text: 'select id from songs where id = $1;',
      values: [songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Song dengan id: ${songId} tidak ditemukan`);
      throw e;
    }
  }

  async isPlaylistOwnerOrCollaborator(playlistId, userId) {
    const query = {
      text: `
        select
            p.owner_id,
            c.user_id as collaborator_id
        from users as u
        left join playlists as p on u.id = p.owner_id
        left join collaborations as c on p.id = c.playlist_id
        where p.id = $1 and (u.id = $2 or c.user_id = $2);`,
      values: [playlistId, userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthorizationError(
        `User dengan id: ${userId} tidak berhak mengakses playlist dengan id: ${playlistId}`,
      );
      throw e;
    }
  }

  async isHavePlaylistWriteAccess(playlistId, userId) {
    await this.isPlaylistExist(playlistId);
    const query = {
      text: `
        select u.id
        from users as u
        inner join playlists as p on u.id = p.owner_id
        where p.id = $1 and u.id = $2;`,
      values: [playlistId, userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthorizationError(
        `User dengan id: ${userId} tidak berhak mengakses playlist dengan id: ${playlistId}`,
      );
      throw e;
    }
  }

  async addToActivity(playlistId, userId, action, songId) {
    await this.isPlaylistExist(playlistId);
    await this.isSongExist(songId);
    const id = nanoid(16);
    const query = {
      text: 'insert into playlist_activities(id, playlist_id, user_id, action, song_id) values ($1, $2, $3, $4, $5);',
      values: [id, playlistId, userId, action, songId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = InvariantError('Activity gagal ditambahkan');
      throw e;
    }
  }
}
module.exports = PlaylistService;

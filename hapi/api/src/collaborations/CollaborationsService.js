const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');
const NotFoundError = require('../core/exceptions/NotFoundError');
const AuthorizationError = require('../core/exceptions/AuthorizationError');

class CollaborationService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addCollaboration(playlistId, userId, ownerId) {
    await this.isUserExist(userId);
    await this.isPlaylistExist(playlistId);
    await this.isPlaylistOwner(playlistId, ownerId);
    const id = nanoid(16);
    const query = {
      text: 'insert into collaborations(id, playlist_id, user_id) values($1, $2, $3) returning id;',
      values: [id, playlistId, userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('collaborations gagal ditambahkan');
      throw e;
    }
    return result.rows[0].id;
  }

  async deleteCollaboration(playlistId, userId, ownerId) {
    await this.isUserExist(userId);
    await this.isPlaylistExist(playlistId);
    await this.isPlaylistOwner(playlistId, ownerId);
    await this.isCollaborationOwner(playlistId, ownerId);
    const query = {
      text: 'delete from collaborations where playlist_id = $1 and user_id = $2 returning id;',
      values: [playlistId, userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('collaborations gagal dihapus');
      throw e;
    }
    return result.rows[0].id;
  }

  async isPlaylistOwner(playlistId, ownerId) {
    const query = {
      text: 'select p.id from playlists as p where p.id = $1 and p.owner_id = $2',
      values: [playlistId, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthorizationError(
        `User dengan ownerId: ${ownerId} tidak berhak mengakses playlist dengan playlistId: ${playlistId}`,
      );
      throw e;
    }
  }

  async isUserExist(userId) {
    const query = {
      text: 'select id from users where id = $1',
      values: [userId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`User dengan userId: ${userId} tidak ditemukan`);
      throw e;
    }
  }

  async isPlaylistExist(playlistId) {
    const query = {
      text: 'select id from playlists where id = $1',
      values: [playlistId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Playlist dengan playlistId: ${playlistId} tidak ditemukan`);
      throw e;
    }
  }

  async isCollaborationOwner(playlistId, ownerId) {
    const query = {
      text: 'select from users as u inner join playlists as p on u.id = p.owner_id inner join collaborations as c on p.id = c.playlist_id where p.id = $1 and u.id = $2',
      values: [playlistId, ownerId],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new NotFoundError(`Playlist dengan playlistId: ${playlistId} tidak ditemukan`);
      throw e;
    }
  }
}

module.exports = CollaborationService;

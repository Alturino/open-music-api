const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');

class AuthenticationService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addRefreshToken(token) {
    const id = nanoid(16);
    const query = {
      text: 'insert into authentications(id, token) values($1, $2) returning id;',
      values: [id, token],
    };

    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('song gagal ditambahkan');
      throw e;
    }

    return result.rows[0].id;
  }

  async verifyRefreshToken(token) {
    const query = {
      text: 'select token from authentications where token = $1',
      values: [token],
    };

    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError(`Token dengan token: ${token} tidak ditemukan`);
      throw e;
    }
  }

  async deleteRefreshToken(token) {
    const query = {
      text: 'delete from authentications where token = $1 returning id;',
      values: [token],
    };

    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError(`Token dengan token: ${token} gagal dihapus`);
      throw e;
    }

    return result.rows[0].id;
  }
}
module.exports = AuthenticationService;

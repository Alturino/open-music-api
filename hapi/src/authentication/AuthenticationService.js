const { nanoid } = require('nanoid');
const InvariantError = require('../core/exceptions/InvariantError');

class AuthenticationService {
  constructor(pgPool) {
    this._pool = pgPool;
  }

  async addRefreshToken(token) {
    console.log(`AuthenticationService addRefreshToken token: ${token}`);
    const id = nanoid(16);
    const query = {
      text: 'insert into authentications(id, token) values($1, $2) returning id;',
      values: [id, token],
    };

    console.log(
      `AuthenticationService addRefreshToken inserting query: ${query.text}, params: ${query.values}`,
    );
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError('song gagal ditambahkan');
      console.error(`SongService addSong ${e}`);
      throw e;
    }

    console.log(
      `AuthenticationService addRefreshToken query: ${query.text}, params: ${query.values}, ${JSON.stringify(result.rows)} inserted`,
    );
    return result.rows[0].id;
  }

  async verifyRefreshToken(token) {
    console.log(`AuthenticationService verifyRefreshToken token: ${token} `);
    const query = {
      text: 'select token from authentications where token = $1',
      values: [token],
    };

    console.log(
      `AuthenticationService verifyRefreshToken executing query: ${query.text}, params: ${query.values}`,
    );
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError(`Token dengan token: ${token} tidak ditemukan`);
      console.error(`AuthenticationService verifyRefreshToken ${e}`);
      throw e;
    }
    console.log(
      `AuthenticationService verifyRefreshToken query: ${query.text}, params: ${query.values} executed`,
    );
  }

  async deleteRefreshToken(token) {
    console.log(`AuthenticationService deleteRefreshToken token: ${token}`);
    const query = {
      text: 'delete from authentications where token = $1 returning id;',
      values: [token],
    };

    const result = await this._pool.query(query);
    console.log(
      `AuthenticationService deleteRefreshToken executing query: ${query.text}, params: ${query.values}`,
    );
    if (result.rowCount === null || result.rowCount < 1) {
      const e = new InvariantError(`Token dengan token: ${token} gagal dihapus`);
      console.error(`AuthenticationService deleteRefreshToken ${e}`);
      throw e;
    }
    console.log(`${JSON.stringify(result)}`);

    console.log(
      `AuthenticationService deleteRefreshToken query: ${query.text}, params: ${query.values} executed`,
    );
    return result.rows[0].id;
  }
}
module.exports = AuthenticationService;

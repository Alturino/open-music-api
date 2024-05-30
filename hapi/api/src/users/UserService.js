const { nanoid } = require('nanoid');
const bcrypt = require('bcrypt');
const InvariantError = require('../core/exceptions/InvariantError');
const AuthenticationError = require('../core/exceptions/AuthenticationError');

class UserService {
  constructor(pgPool) {
    this._pool = pgPool;

    this.addUser = this.addUser.bind(this);
    this.getUserById = this.getUserById.bind(this);
  }

  async addUser(username, password, fullname) {
    const queryIsExist = {
      text: 'select id from users where username = $1',
      values: [username],
    };
    const resultQueryIsExist = await this._pool.query(queryIsExist);
    if (resultQueryIsExist.rowCount > 0) {
      const e = new InvariantError(`user dengan username: ${username} sudah terdaftar`);
      throw e;
    }

    const id = nanoid(16);
    const hashedPassword = await bcrypt.hash(password, 10);
    const insertUserQuery = {
      text: 'insert into users(id, username, password, fullname) values($1, $2, $3, $4) returning id;',
      values: [id, username, hashedPassword, fullname],
    };
    const result = await this._pool.query(insertUserQuery);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('user gagal ditambahkan');
      throw e;
    }
    return result.rows[0].id;
  }

  async getUserById(id) {
    const query = {
      text: 'select username, password, fullname from users where id = $1;',
      values: [id],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError(`user dengan id: ${id} tidak ditemukan`, 404);
      throw e;
    }
    const users = result.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        year: row.year,
        genre: row.genre,
        performer: row.performer,
        duration: row.duration,
      };
    });

    return users[0];
  }

  async verifyUserCredential(username, password) {
    const query = {
      text: 'select id, password from users where username = $1',
      values: [username],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthenticationError(`user with username: ${username} is not found`);
      throw e;
    }
    const { id, password: hashedPassword } = result.rows[0];
    const isMatch = await bcrypt.compare(password, hashedPassword);
    if (!isMatch) {
      const e = new AuthenticationError(
        `UserService verifyUserCredential password: ${password} not match with the database`,
      );
      throw e;
    }
    return id;
  }
}
module.exports = UserService;

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
    console.log(
      `UserService addUser username: ${username}, password: ${password}, fullname: ${fullname}`,
    );
    const queryIsExist = {
      text: 'select id from users where username = $1',
      values: [username],
    };
    const resultQueryIsExist = await this._pool.query(queryIsExist);
    if (resultQueryIsExist.rowCount > 0) {
      const e = new InvariantError(`user dengan username: ${username} sudah terdaftar`);
      console.error(`UserService addUser ${e}`);
      throw e;
    }

    const id = nanoid(16);
    const hashedPassword = await bcrypt.hash(password, 10);
    const insertUserQuery = {
      text: 'insert into users(id, username, password, fullname) values($1, $2, $3, $4) returning id;',
      values: [id, username, hashedPassword, fullname],
    };
    console.log(
      `UserService addUser executing query: ${insertUserQuery.text}, params: ${insertUserQuery.values}`,
    );
    const result = await this._pool.query(insertUserQuery);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new InvariantError('user gagal ditambahkan');
      console.error(`UserService addUser ${e}`);
      throw e;
    }
    console.log(
      `UserService addUser query: ${insertUserQuery.text}, params: ${insertUserQuery.values} executed`,
    );
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
      console.error(`UserService getUserById ${e}`);
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

    console.log(JSON.stringify(users));
    return users[0];
  }

  async verifyUserCredential(username, password) {
    console.log(`UserService verifyUserCredential username: ${username}, password: ${password}`);
    const query = {
      text: 'select id, password from users where username = $1',
      values: [username],
    };
    const result = await this._pool.query(query);
    if (result.rowCount === null || result.rowCount === 0) {
      const e = new AuthenticationError(`user with username: ${username} is not found`);
      console.log(e);
      throw e;
    }
    const { id, password: hashedPassword } = result.rows[0];
    const isMatch = await bcrypt.compare(password, hashedPassword);
    if (!isMatch) {
      const e = new AuthenticationError(
        `UserService verifyUserCredential password: ${password} not match with the database`,
      );
      console.log(e);
      throw e;
    }
    return id;
  }
}
module.exports = UserService;

class UserHandler {
  constructor(service, validator) {
    this._service = service;
    this._validator = validator;

    this.addUser = this.addUser.bind(this);
    this.getUserById = this.getUserById.bind(this);
  }

  async addUser(req, h) {
    this._validator.validatePayload(req.payload);
    const { username, password, fullname } = req.payload;
    const id = await this._service.addUser(username, password, fullname);
    return h
      .response({
        status: 'success',
        message: 'User berhasil ditambahkan',
        data: { userId: id },
      })
      .code(201);
  }

  async getUserById(req, h) {
    const { id } = req.params;
    const user = await this._service.getUserById({ id });
    return h
      .response({
        status: 'success',
        message: `User dengan id: ${id} ditemukan`,
        data: { user },
      })
      .code(200);
  }

  async getUsers(req, h) {
    const { title, performer } = req.query;
    const users = await this._service.getUsers({ title, performer });
    return h
      .response({
        status: 'success',
        message: `User ditemukan`,
        data: { users },
      })
      .code(200);
  }
}

module.exports = UserHandler;

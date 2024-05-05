class AlbumHandler {
  constructor(service, validator) {
    this._service = service;
    this._validator = validator;

    this.addAlbum = this.addAlbum.bind(this);
    this.updateAlbum = this.updateAlbum.bind(this);
    this.deleteAlbum = this.deleteAlbum.bind(this);
    this.getAlbumById = this.getAlbumById.bind(this);
    this.getAlbums = this.getAlbums.bind(this);
  }

  async addAlbum(req, h) {
    this._validator.validatePayload(req.payload);
    const { name, year } = req.payload;
    const id = await this._service.addAlbum({ name, year });
    return h
      .response({
        status: 'success',
        message: 'Album berhasil ditambahkan',
        data: { albumId: id },
      })
      .code(201);
  }

  async updateAlbum(req, h) {
    this._validator.validatePayload(req.payload);
    const { name, year } = req.payload;
    const { id } = req.params;
    await this._service.updateAlbum({ id, name, year });
    return h
      .response({
        status: 'success',
        message: 'Album berhasil diperbarui',
        data: { albumId: id },
      })
      .code(200);
  }

  async getAlbumById(req, h) {
    const { id } = req.params;
    const album = await this._service.getAlbumById({ id });
    return h
      .response({
        status: 'success',
        message: 'Album ditemukan',
        data: { album },
      })
      .code(200);
  }

  async deleteAlbum(req, h) {
    const { id } = req.params;
    const album = await this._service.deleteAlbum({ id });
    return h
      .response({
        status: 'success',
        message: 'Album berhasil dihapus',
        data: { album },
      })
      .code(200);
  }

  async getAlbums(req, h) {
    const albums = await this._service.getAlbums();
    return h
      .response({
        status: 'success',
        message: 'Album tidak kosong',
        data: { albums },
      })
      .code(200);
  }
}

module.exports = AlbumHandler;

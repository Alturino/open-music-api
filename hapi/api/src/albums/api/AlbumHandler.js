const config = require('../../core/config');
class AlbumHandler {
  constructor(albumService, storageService, albumValidator) {
    this._albumService = albumService;
    this._storageService = storageService;
    this._albumValidator = albumValidator;

    this.addAlbum = this.addAlbum.bind(this);
    this.updateAlbum = this.updateAlbum.bind(this);
    this.deleteAlbum = this.deleteAlbum.bind(this);
    this.getAlbumById = this.getAlbumById.bind(this);
    this.getAlbums = this.getAlbums.bind(this);
    this.addCoverToAlbum = this.addCoverToAlbum.bind(this);
  }

  async addAlbum(req, h) {
    this._albumValidator.validatePayload(req.payload);
    const { name, year } = req.payload;
    const id = await this._albumService.addAlbum(name, year);
    return h
      .response({
        status: 'success',
        message: 'Album berhasil ditambahkan',
        data: { albumId: id },
      })
      .code(201);
  }

  async updateAlbum(req, h) {
    this._albumValidator.validatePayload(req.payload);
    const { name, year } = req.payload;
    const { id } = req.params;
    await this._albumService.updateAlbum(id, name, year);
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
    const album = await this._albumService.getAlbumById(id);
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
    await this._albumService.deleteAlbum(id);
    return h
      .response({
        status: 'success',
        message: 'Album berhasil dihapus',
      })
      .code(200);
  }

  async getAlbums(req, h) {
    const albums = await this._albumService.getAlbums();
    return h
      .response({
        status: 'success',
        message: 'Album tidak kosong',
        data: { albums },
      })
      .code(200);
  }

  async addCoverToAlbum(req, h) {
    const { data } = req.payload;
    this._albumValidator.validateImageHeaders(data.hapi.headers);
    const filename = await this._storageService.writeFile(data, data.hapi);
    const { id: albumId } = req.params;
    await this._albumService.addCoverToAlbum(albumId, filename);
    return h.response({
      status: 'success',
      message: 'Cover album berhasil ditambahkan',
      data: {
        file: `http://${config.app.host}:${config.app.port}/albums/${albumId}/images/${filename}`,
      },
    });
  }
}

module.exports = AlbumHandler;

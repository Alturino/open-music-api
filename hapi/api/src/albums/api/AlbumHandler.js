const config = require('../../core/config');

class AlbumHandler {
  constructor(albumService, storageService, albumValidator, folder) {
    this._albumService = albumService;
    this._storageService = storageService;
    this._albumValidator = albumValidator;
    this._folder = folder;

    this.addAlbum = this.addAlbum.bind(this);
    this.updateAlbum = this.updateAlbum.bind(this);
    this.deleteAlbum = this.deleteAlbum.bind(this);
    this.getAlbumById = this.getAlbumById.bind(this);
    this.getAlbums = this.getAlbums.bind(this);
    this.addCoverToAlbum = this.addCoverToAlbum.bind(this);
    this.addAlbumLike = this.addAlbumLike.bind(this);
    this.unlikeAlbum = this.unlikeAlbum.bind(this);
    this.getAlbumLike = this.getAlbumLike.bind(this);
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
    const { cover } = req.payload;
    this._albumValidator.validateImageHeaders(cover.hapi.headers);

    const filename = await this._storageService.writeFile(cover, cover.hapi);
    const coverUrl = `${config.app.host}:${config.app.port}/albums/covers/${filename}`;

    const { id: albumId } = req.params;
    await this._albumService.addCoverToAlbum(albumId, coverUrl);
    return h
      .response({
        status: 'success',
        message: 'Cover album berhasil ditambahkan',
        data: {
          file: coverUrl,
        },
      })
      .code(201);
  }

  async addAlbumLike(req, h) {
    const { id: userId } = req.auth.credentials;
    const { id: albumId } = req.params;
    req.log(
      ['INF', 'AlbumHandler', 'addAlbumLike'],
      `albumId=${albumId} userId=${userId} msg=initiate like`,
    );
    await this._albumService.addAlbumLike(req, userId, albumId);
    req.log(
      ['INF', 'AlbumHandler', 'addAlbumLike'],
      `albumId=${albumId} userId=${userId} msg=success`,
    );
    return h
      .response({
        status: 'success',
        message: `Like albumId:${albumId} dari userId:${userId} berhasil`,
      })
      .code(201);
  }

  async getAlbumLike(req, h) {
    const { id: albumId } = req.params;
    req.log(['INF', 'AlbumHandler', 'getAlbumLike'], `albumId=${albumId} msg=get album like_count`);
    const likeCount = await this._albumService.getAlbumLike(req, albumId);
    req.log(
      ['INF', 'AlbumHandler', 'addAlbumLike'],
      `albumId=${albumId} like_count=${likeCount} msg=success get album like_count`,
    );
    return h.response({
      status: 'success',
      message: `Album like_count = ${likeCount}`,
      data: { likes: likeCount },
    });
  }

  async unlikeAlbum(req, h) {
    const { id: albumId } = req.params;
    const { id: userId } = req.auth.credentials;
    req.log(['INF', 'AlbumHandler', 'getAlbumLike'], `albumId=${albumId} msg=get album like_count`);
    await this._albumService.unlikeAlbum(req, userId, albumId);
    req.log(
      ['INF', 'AlbumHandler', 'addAlbumLike'],
      `albumId=${albumId} msg=deleted album with albumId=${albumId}`,
    );
    return h.response({
      status: 'success',
      message: `Album with albumId=${albumId} successfully deleted`,
    });
  }
}

module.exports = AlbumHandler;

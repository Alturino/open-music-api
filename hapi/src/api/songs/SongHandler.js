class SongHandler {
  constructor(service, validator) {
    this._service = service;
    this._validator = validator;

    this.addSong = this.addSong.bind(this);
    this.updateSong = this.updateSong.bind(this);
    this.deleteSong = this.deleteSong.bind(this);
    this.getSongById = this.getSongById.bind(this);
    this.getSongs = this.getSongs.bind(this);
  }

  async addSong(req, h) {
    this._validator.validatePayload(req.payload);
    const { title, year, genre, performer, duration, albumId } = req.payload;
    const id = await this._service.addSong({ title, year, genre, performer, duration, albumId });
    return h
      .response({
        status: 'success',
        message: 'Song berhasil ditambahkan',
        data: { songId: id },
      })
      .code(201);
  }

  async updateSong(req, h) {
    this._validator.validatePayload(req.payload);
    const { title, year, genre, performer, duration, albumId } = req.payload;
    const { id } = req.params;
    const song = await this._service.updateSong({
      id,
      title,
      year,
      genre,
      performer,
      duration,
      albumId,
    });
    return h
      .response({
        status: 'success',
        message: 'Song berhasil diperbarui',
        data: { song },
      })
      .code(200);
  }

  async getSongById(req, h) {
    const { id } = req.params;
    const song = await this._service.getSongById({ id });
    return h
      .response({
        status: 'success',
        message: `Song dengan id: ${id} ditemukan`,
        data: { song },
      })
      .code(200);
  }

  async getSongs(req, h) {
    const { title, performer } = req.query;
    const songs = await this._service.getSongs({ title, performer });
    return h
      .response({
        status: 'success',
        message: `Song ditemukan`,
        data: { songs },
      })
      .code(200);
  }

  async deleteSong(req, h) {
    const { id } = req.params;
    const song = await this._service.deleteSong({ id });
    return h
      .response({
        status: 'success',
        message: `Song dengan id: ${id} dihapus`,
        data: { song },
      })
      .code(200);
  }
}

module.exports = SongHandler;

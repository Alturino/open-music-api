class PlaylistHandler {
  constructor(service, validator) {
    this._service = service;
    this._validator = validator;

    this.addPlaylist = this.addPlaylist.bind(this);
    this.getPlaylists = this.getPlaylists.bind(this);
    this.deletePlaylist = this.deletePlaylist.bind(this);
    this.addSongToPlaylist = this.addSongToPlaylist.bind(this);
    this.getSongsInPlaylist = this.getSongsInPlaylist.bind(this);
    this.deleteSongInPlaylist = this.deleteSongInPlaylist.bind(this);
  }

  async addPlaylist(req, h) {
    this._validator.validatePlaylist(req.payload);
    const { name } = req.payload;
    const { id: ownerId } = req.auth.credentials;
    const playlistId = await this._service.addPlaylist(name, ownerId);
    return h
      .response({
        status: 'success',
        message: 'Playlist berhasil ditambahkan',
        data: { playlistId },
      })
      .code(201);
  }

  async getPlaylists(req, h) {
    const { id: userId } = req.auth.credentials;
    const playlists = await this._service.getPlaylists(userId);
    return h
      .response({
        status: 'success',
        message: 'Playlists ditemukan',
        data: { playlists },
      })
      .code(200);
  }

  async deletePlaylist(req, h) {
    const { id } = req.params;
    const { id: userId } = req.auth.credentials;
    const deletedPlaylistId = await this._service.deletePlaylist(id, userId);
    return h
      .response({
        status: 'success',
        message: `Playlists dengan id: ${id} berhasil dihapus`,
        data: { playlistId: deletedPlaylistId },
      })
      .code(200);
  }

  async addSongToPlaylist(req, h) {
    this._validator.validatePostSongPlaylist(req.payload);
    const { id: playlistId } = req.params;
    const { id: ownerId } = req.auth.credentials;
    const { songId } = req.payload;
    await this._service.addSongToPlaylist(playlistId, songId, ownerId);
    return h
      .response({
        status: 'success',
        message: `Song di dalam playlist dengan id: ${playlistId} berhasil ditambahkan`,
        data: { songId },
      })
      .code(201);
  }

  async getSongsInPlaylist(req, h) {
    const { id: playlistId } = req.params;
    const { id: userId } = req.auth.credentials;
    const playlist = await this._service.getSongsInPlaylist(playlistId, userId);
    return h
      .response({
        status: 'success',
        message: `Song di dalam playlist dengan id: ${playlistId} ditemukan`,
        data: { playlist },
      })
      .code(200);
  }

  async deleteSongInPlaylist(req, h) {
    this._validator.validateDeleteSongPlaylist(req.payload);
    const { id: playlistId } = req.params;
    const { id: userId } = req.auth.credentials;
    const { songId } = req.payload;
    await this._service.deleteSongInPlaylist(playlistId, userId, songId);
    return h
      .response({
        status: 'success',
        message: `Song di dalam playlist dengan id: ${playlistId} berhasil di hapus`,
        data: { playlistId, songId },
      })
      .code(200);
  }
}

module.exports = PlaylistHandler;

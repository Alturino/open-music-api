class ExportHandler {
  constructor(exportService, playlistService, validator) {
    this._exportService = exportService;
    this._playlistService = playlistService;
    this._validator = validator;

    this.createExport = this.createExport.bind(this);
  }

  async createExport(req, h) {
    this._validator.validatePayload(req.payload);
    const { targetEmail } = req.payload;
    const { id: playlistId } = req.params;
    const { id: userId } = req.auth.credentials;
    await this._playlistService.isHavePlaylistWriteAccess(playlistId, userId);
    await this._exportService.sendMessage(playlistId, targetEmail);
    return h
      .response({
        status: 'success',
        message: `Playlist dengan id: ${playlistId} berhasil diexport ke email ${targetEmail}`,
      })
      .code(200);
  }
}

module.exports = ExportHandler;

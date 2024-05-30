class CollaborationHandler {
  constructor(service, validator) {
    this._service = service;
    this._validator = validator;

    this.addCollaboration = this.addCollaboration.bind(this);
    this.deleteCollaboration = this.deleteCollaboration.bind(this);
  }

  async addCollaboration(req, h) {
    this._validator.validatePostPayload(req.payload);
    const { id: ownerId } = req.auth.credentials;
    const { playlistId, userId } = req.payload;
    const collaborationId = await this._service.addCollaboration(playlistId, userId, ownerId);
    return h
      .response({
        status: 'success',
        message: `Collaboration dengan playlistId: ${playlistId}, userId: ${userId} berhasil ditambahkan`,
        data: { collaborationId },
      })
      .code(201);
  }

  async deleteCollaboration(req, h) {
    this._validator.validateDeletePayload(req.payload);
    const { id: ownerId } = req.auth.credentials;
    const { playlistId, userId } = req.payload;
    const collaborationId = await this._service.deleteCollaboration(playlistId, userId, ownerId);
    return h
      .response({
        status: 'success',
        message: `Collaboration dengan playlistId: ${playlistId}, userId: ${userId} berhasil dihapus`,
        data: { collaborationId },
      })
      .code(200);
  }
}
module.exports = CollaborationHandler;

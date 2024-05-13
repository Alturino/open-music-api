const InvariantError = require('../../core/exceptions/InvariantError');
const {
  PlaylistPayloadSchema,
  PostSongPlaylistPayloadSchema,
  DeleteSongPlaylistPayloadSchema,
} = require('./PlaylistPayloadSchema');

const PlaylistValidator = {
  validatePlaylist: (payload) => {
    const validationResult = PlaylistPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
  validatePostSongPlaylist: (payload) => {
    const validationResult = PostSongPlaylistPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
  validateDeleteSongPlaylist: (payload) => {
    const validationResult = DeleteSongPlaylistPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = PlaylistValidator;

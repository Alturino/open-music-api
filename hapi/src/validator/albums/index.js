const InvariantError = require('../../exceptions/InvariantError');
const AlbumPayloadSchema = require('./AlbumPayloadSchema');

const AlbumValidator = {
  validatePayload: (payload) => {
    const validationResult = AlbumPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = AlbumValidator;

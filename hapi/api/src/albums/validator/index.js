const InvariantError = require('../../core/exceptions/InvariantError');
const { AlbumPayloadSchema, UploadPayloadSchema } = require('./schema');

const AlbumValidator = {
  validatePayload: (payload) => {
    const validationResult = AlbumPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
  validateImageHeaders: (headers) => {
    const validationResult = UploadPayloadSchema.validate(headers);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = AlbumValidator;

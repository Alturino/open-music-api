const InvariantError = require('../../core/exceptions/InvariantError');
const SongPayloadSchema = require('./SongPayloadSchema');

const SongValidator = {
  validatePayload: (payload) => {
    const validationResult = SongPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = SongValidator;
const InvariantError = require('../../core/exceptions/InvariantError');
const ExportPayloadSchema = require('./ExportPayloadSchema');

const ExportValidator = {
  validatePayload: (payload) => {
    const validationResult = ExportPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = ExportValidator;

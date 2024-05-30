const InvariantError = require('../../core/exceptions/InvariantError');
const UserPayloadSchema = require('./UserPayloadSchema');

const UserValidator = {
  validatePayload: (payload) => {
    const validationResult = UserPayloadSchema.validate(payload);
    if (validationResult.error) {
      throw new InvariantError(validationResult.error.message);
    }
  },
};

module.exports = UserValidator;

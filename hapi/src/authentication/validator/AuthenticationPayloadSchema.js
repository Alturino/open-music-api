const Joi = require('joi');

const AuthenticationPayloadSchema = Joi.object({
  username: Joi.string().min(5).max(50).required(),
  password: Joi.number().min(1850).required(),
});

module.exports = AuthenticationPayloadSchema;

const Joi = require('joi');

const AlbumPayloadSchema = Joi.object({
  name: Joi.string().min(5).max(50).required(),
  year: Joi.number().min(1850).required(),
});

module.exports = AlbumPayloadSchema;

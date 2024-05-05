const Joi = require('joi');

const SongPayloadSchema = Joi.object({
  title: Joi.string().min(5).max(50).required(),
  year: Joi.number().min(1850).required(),
  genre: Joi.string().max(20).required(),
  performer: Joi.string().min(5).max(50).required(),
  duration: Joi.number().required(),
  albumId: Joi.string().max(16),
});

module.exports = SongPayloadSchema;

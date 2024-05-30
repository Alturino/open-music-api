const Joi = require('joi');

const AlbumPayloadSchema = Joi.object({
  name: Joi.string().min(5).max(50).required(),
  year: Joi.number().min(1850).required(),
});

const UploadPayloadSchema = Joi.object({
  'content-type': Joi.string()
    .valid('image/apng', 'image/avif', 'image/gif', 'image/jpeg', 'image/png', 'image/webp')
    .required(),
}).unknown();

module.exports = { AlbumPayloadSchema, UploadPayloadSchema };

const Joi = require('joi');

const PlaylistPayloadSchema = Joi.object({
  name: Joi.string().required(),
});

const PostSongPlaylistPayloadSchema = Joi.object({
  songId: Joi.string().required(),
});

const DeleteSongPlaylistPayloadSchema = Joi.object({
  songId: Joi.string().required(),
});

module.exports = {
  PlaylistPayloadSchema,
  PostSongPlaylistPayloadSchema,
  DeleteSongPlaylistPayloadSchema,
};

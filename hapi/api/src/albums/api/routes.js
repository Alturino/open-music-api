const path = require('path');
const albumsRoutes = (handler) => [
  {
    method: 'POST',
    path: '/albums',
    handler: handler.addAlbum,
  },
  {
    method: 'GET',
    path: '/albums/{id}',
    handler: handler.getAlbumById,
  },
  {
    method: 'GET',
    path: '/albums',
    handler: handler.getAlbums,
  },
  {
    method: 'PUT',
    path: '/albums/{id}',
    handler: handler.updateAlbum,
  },
  {
    method: 'DELETE',
    path: '/albums/{id}',
    handler: handler.deleteAlbum,
  },
  {
    method: 'POST',
    path: '/albums/{id}/likes',
    handler: handler.addAlbumLike,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'GET',
    path: '/albums/{id}/likes',
    handler: handler.getAlbumLike,
  },
  {
    method: 'DELETE',
    path: '/albums/{id}/likes',
    handler: handler.unlikeAlbum,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'POST',
    path: '/albums/{id}/covers',
    handler: handler.addCoverToAlbum,
    options: {
      payload: {
        allow: 'multipart/form-data',
        multipart: true,
        output: 'stream',
        parse: true,
        maxBytes: 500 * 1000,
      },
    },
  },
  {
    method: 'GET',
    path: '/albums/covers/{filename*}',
    handler: {
      directory: {
        path: path.resolve(__dirname, '../../public/assets'),
      },
    },
  },
];

module.exports = albumsRoutes;

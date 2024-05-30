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
    path: '/albums/{id}/covers',
    handler: handler.addCoverToAlbum,
    options: {
      payload: {
        allow: 'multipart/form-data',
        multipart: true,
        output: 'stream',
        parse: true,
      },
    },
  },
];

module.exports = albumsRoutes;

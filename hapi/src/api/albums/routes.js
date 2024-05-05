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
];

module.exports = albumsRoutes;

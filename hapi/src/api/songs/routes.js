const songsRoutes = (handler) => [
  {
    method: 'POST',
    path: '/songs',
    handler: handler.addSong,
  },
  {
    method: 'GET',
    path: '/songs/{id}',
    handler: handler.getSongById,
  },
  {
    method: 'GET',
    path: '/songs',
    handler: handler.getSongs,
  },
  {
    method: 'PUT',
    path: '/songs/{id}',
    handler: handler.updateSong,
  },
  {
    method: 'DELETE',
    path: '/songs/{id}',
    handler: handler.deleteSong,
  },
];

module.exports = songsRoutes;

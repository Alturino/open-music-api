const exportRoutes = (handler) => [
  {
    method: 'POT',
    path: '/export/playlists/{id}',
    handler: handler.createExport,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
];
module.exports = exportRoutes;

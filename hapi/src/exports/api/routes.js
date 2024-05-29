const exportRoutes = (handler) => [
  {
    method: 'POT',
    path: '/export/playlists/{id}',
    handler: handler.createExport,
  },
];
module.exports = exportRoutes;

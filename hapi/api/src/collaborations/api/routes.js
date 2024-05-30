const collaborationsRoutes = (handler) => [
  {
    method: 'POST',
    path: '/collaborations',
    handler: handler.addCollaboration,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'DELETE',
    path: '/collaborations',
    handler: handler.deleteCollaboration,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
];

module.exports = collaborationsRoutes;

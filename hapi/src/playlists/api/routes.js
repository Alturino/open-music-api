const playlistRoutes = (handler) => [
  {
    method: 'POST',
    path: '/playlists',
    handler: handler.addPlaylist,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'GET',
    path: '/playlists',
    handler: handler.getPlaylists,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'DELETE',
    path: '/playlists/{id}',
    handler: handler.deletePlaylist,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'POST',
    path: '/playlists/{id}/songs',
    handler: handler.addSongToPlaylist,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'GET',
    path: '/playlists/{id}/songs',
    handler: handler.getSongsInPlaylist,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'DELETE',
    path: '/playlists/{id}/songs',
    handler: handler.deleteSongInPlaylist,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
  {
    method: 'GET',
    path: '/playlists/{id}/activities',
    handler: handler.getActivities,
    options: {
      auth: 'open_music_api_jwt',
    },
  },
];

module.exports = playlistRoutes;

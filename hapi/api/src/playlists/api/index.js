const PlaylistHandler = require('./PlaylistHandler');
const routes = require('./routes');
const playlistPlugin = {
  name: 'playlists',
  version: '1.0.0',
  register: async (server, { service, validator }) => {
    const playlistHandler = new PlaylistHandler(service, validator);
    server.route(routes(playlistHandler));
  },
};

module.exports = playlistPlugin;

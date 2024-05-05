const SongHandler = require('./SongHandler');
const routes = require('./routes');
const songsPlugin = {
  name: 'songs',
  version: '1.0.0',
  register: async (server, { service, validator }) => {
    const albumHandler = new SongHandler(service, validator);
    server.route(routes(albumHandler));
  },
};

module.exports = songsPlugin;

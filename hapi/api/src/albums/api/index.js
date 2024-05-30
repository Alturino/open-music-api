const AlbumHandler = require('./AlbumHandler');
const routes = require('./routes');
const albumPlugin = {
  name: 'albums',
  version: '1.0.0',
  register: async (server, { albumService, storageService, albumValidator }) => {
    const albumHandler = new AlbumHandler(albumService, storageService, albumValidator);
    server.route(routes(albumHandler));
  },
};

module.exports = albumPlugin;

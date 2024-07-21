const AlbumHandler = require('./AlbumHandler');
const routes = require('./routes');
const albumPlugin = {
  name: 'albums',
  version: '1.0.0',
  register: async (server, { albumService, storageService, albumValidator, folder }) => {
    const albumHandler = new AlbumHandler(albumService, storageService, albumValidator, folder);
    server.route(routes(albumHandler));
  },
};

module.exports = albumPlugin;

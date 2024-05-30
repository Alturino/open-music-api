const ExportHandler = require('./ExportHandler');
const routes = require('./routes');
const exportsPlugin = {
  name: 'exports',
  version: '1.0.0',
  register: async (server, { producerService, playlistService, validator }) => {
    const exportHandler = new ExportHandler(producerService, playlistService, validator);
    server.route(routes(exportHandler));
  },
};

module.exports = exportsPlugin;

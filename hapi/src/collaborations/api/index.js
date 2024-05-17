const CollaborationHandler = require('./CollaborationHandler');
const routes = require('./routes');
const collaborationsPlugin = {
  name: 'collaborations',
  version: '1.0.0',
  register: async (server, { service, validator }) => {
    const collaborationHandler = new CollaborationHandler(service, validator);
    server.route(routes(collaborationHandler));
  },
};

module.exports = collaborationsPlugin;

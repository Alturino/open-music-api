const AuthenticationHandler = require('./AuthenticationHandler');
const routes = require('./routes');
const authenticationPlugin = {
  name: 'authentications',
  version: '1.0.0',
  register: async (server, { authenticationService, userService, tokenManager, validator }) => {
    const authenticationHandler = new AuthenticationHandler(
      authenticationService,
      userService,
      tokenManager,
      validator,
    );
    server.route(routes(authenticationHandler));
  },
};

module.exports = authenticationPlugin;

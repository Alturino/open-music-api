const Jwt = require('@hapi/jwt');
const InvariantError = require('../../core/exceptions/InvariantError');
const config = require('../../core/config');
const TokenManager = {
  generateAccessToken: (payload) => Jwt.token.generate(payload, config.app.accessTokenKey, {}),
  generateRefreshToken: (payload) => Jwt.token.generate(payload, config.app.refreshTokenKey, {}),
  verifyRefreshToken: (refreshToken) => {
    try {
      const artifact = Jwt.token.decode(refreshToken);
      Jwt.token.verifySignature(artifact, config.app.refreshTokenKey);
      const { payload } = artifact.decoded;
      return payload;
    } catch (e) {
      throw new InvariantError('Refresh token tidak valid');
    }
  },
};

module.exports = TokenManager;

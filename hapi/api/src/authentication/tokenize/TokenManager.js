const Jwt = require('@hapi/jwt');
const InvariantError = require('../../core/exceptions/InvariantError');
const config = require('../../core/config');
const TokenManager = {
  generateAccessToken: (payload) => Jwt.token.generate(payload, config.app.accessTokenKey, {}),
  generateRefreshToken: (payload) => Jwt.token.generate(payload, config.app.refreshTokenKey, {}),
  verifyRefreshToken: (refreshToken) => {
    try {
      const artifact = Jwt.token.decode(refreshToken);
      Jwt.token.verifySignature(artifact, process.env.REFRESH_TOKEN_KEY);
      const { payload } = artifact.decoded;
      return payload;
    } catch (e) {
      throw new InvariantError('Refresh token tidak valid');
    }
  },
};

module.exports = TokenManager;

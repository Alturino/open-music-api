class AuthenticationHandler {
  constructor(authenticationService, userService, tokenManager, validator) {
    this._authenticationService = authenticationService;
    this._userService = userService;
    this._validator = validator;
    this._tokenManager = tokenManager;

    this.postAuthenticationHandler = this.postAuthenticationHandler.bind(this);
    this.putAuthenticationHandler = this.putAuthenticationHandler.bind(this);
    this.deleteAuthenticationHandler = this.deleteAuthenticationHandler.bind(this);
  }

  async postAuthenticationHandler(req, h) {
    this._validator.validatePostPayload(req.payload);
    const { username, password } = req.payload;
    const id = await this._userService.verifyUserCredential(username, password);
    const accessToken = this._tokenManager.generateAccessToken({ id });
    const refreshToken = this._tokenManager.generateRefreshToken({ id });
    await this._authenticationService.addRefreshToken(refreshToken);
    return h
      .response({
        status: 'success',
        message: 'Authentication berhasil ditambahkan',
        data: {
          accessToken,
          refreshToken,
        },
      })
      .code(201);
  }

  async putAuthenticationHandler(req, h) {
    this._validator.validatePutPayload(req.payload);
    const { refreshToken } = req.payload;
    await this._authenticationService.verifyRefreshToken(refreshToken);
    const { id } = this._tokenManager.verifyRefreshToken(refreshToken);
    const accessToken = this._tokenManager.generateAccessToken({ id });
    return h
      .response({
        status: 'success',
        message: 'AccessToken diperbarui',
        data: {
          accessToken,
        },
      })
      .code(200);
  }

  async deleteAuthenticationHandler(req, h) {
    this._validator.validateDeletePayload(req.payload);
    const { refreshToken } = req.payload;
    await this._authenticationService.verifyRefreshToken(refreshToken);
    await this._authenticationService.deleteRefreshToken(refreshToken);
    return h
      .response({
        status: 'success',
        message: 'RefreshToken berhasil dihapus',
      })
      .code(200);
  }
}
module.exports = AuthenticationHandler;

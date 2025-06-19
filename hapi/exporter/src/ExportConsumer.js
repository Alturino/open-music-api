const logger = require('./core/logger');
class ExportConsumer {
  constructor(service, mailSender) {
    this._service = service;
    this._mailSender = mailSender;

    this.consume = this.consume.bind(this);
  }

  async consume(message) {
    logger.info(`ExportConsumer consume message: ${message.content.toString()}`);
    try {
      const { playlistId, targetEmail } = JSON.parse(message.content.toString());
      const playlists = await this._service.getPlaylistToExport(playlistId);
      logger.info(
        `ExportConsumer consume sending email to: ${targetEmail} with content: ${JSON.stringify(playlists)}`,
      );
      const result = await this._mailSender.sendEmail(targetEmail, JSON.stringify(playlists));
      logger.info('ExportConsumer email sent', result);
      return result;
    } catch (e) {
      console.error('ExportConsumer consume fail sending message with error', e);
    }
  }
}

module.exports = ExportConsumer;

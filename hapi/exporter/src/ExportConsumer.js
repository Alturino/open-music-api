class ExportConsumer {
  constructor(service, mailSender) {
    this._service = service;
    this._mailSender = mailSender;

    this.listen = this.consume.bind(this);
  }

  async consume(message) {
    try {
      const { userId, playlistId, targetEmail } = JSON.parse(message.content.toString());
      const playlists = await this._service.getPlaylistToExport(playlistId, userId);
      console.log(
        `ExportConsumer listen sending email to: ${targetEmail} with content: ${JSON.stringify(
          playlists,
        )}`,
      );
      const result = await this._mailSender.sendEmail(targetEmail, JSON.stringify(playlists));
      console.log(`ExportConsumer email sent`);
      return result;
    } catch (e) {
      console.error(`ExportConsumer listen ${e}`);
    }
  }
}

module.exports = ExportConsumer;

const logger = require('./core/logger');

class ExportMailSender {
  constructor(transport) {
    this._transporter = transport;

    this.sendEmail = this.sendEmail.bind(this);
  }

  sendEmail(email, content) {
    logger.info(`ExportMailSender sendEmail to: ${email} with content: ${content}`);
    const res = this._transporter.sendMail({
      from: 'Open Music Api Exporter',
      to: email,
      subject: 'Export Playlist',
      text: 'Terlampir hasil export playlist',
      attachments: [{ filename: 'playlist.json', content }],
    });
    logger.info(`ExportMailSender sendEmail sent email`);
    return res;
  }
}

module.exports = ExportMailSender;

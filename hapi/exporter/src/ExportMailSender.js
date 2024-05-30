class ExportMailSender {
  constructor(where) {
    this._mailTransport = where;

    this.sendEmail = this.sendEmail.bind(this);
  }

  sendEmail(targetEmail, content) {
    const message = {
      from: 'Playlist Exporter',
      to: targetEmail,
      subject: 'Export Playlist',
      text: 'Terlampir hasil export playlist',
      attachments: [{ filename: 'playlist.json', content }],
    };

    return this._mailTransport.sendEmail(message);
  }
}

module.exports = ExportMailSender;

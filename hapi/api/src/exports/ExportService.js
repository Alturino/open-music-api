class ExportService {
  constructor(mqConection) {
    this._mqConection = mqConection;

    this.sendMessage = this.sendMessage.bind(this);
  }

  async sendMessage(userId, playlistId, targetEmail) {
    try {
      const queue = 'export_mq';
      const channel = await this._mqConection.createChannel();
      await channel.assertQueue(queue, { durable: true });
      const message = { userId, playlistId, targetEmail };
      await channel.sendToQueue(queue, Buffer.from(JSON.stringify(message)));
    } catch (e) {
      console.error(`ProducerService sendMessage failed to send message with error: ${e}`);
    }
  }
}

module.exports = ExportService;

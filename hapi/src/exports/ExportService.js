class ExportService {
  constructor(mqConection) {
    this._mqConection = mqConection;

    this.sendMessage = this.sendMessage.bind(this);
  }

  async sendMessage(queue, message) {
    try {
      const channel = await this._mqConection.createChannel();
      await channel.assertQueue(queue, { durable: true });
      await channel.sendToQueue(queue, Buffer.from(message));
    } catch (e) {
      console.error(`ProducerService sendMessage failed to send message with error: ${e}`);
    }
  }
}

module.exports = ExportService;

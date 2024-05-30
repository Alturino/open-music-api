const ExportConsumer = require('./ExportConsumer');
const amqp = require('amqplib');
const nodemailer = require('nodemailer');
const PlaylistService = require('./PlaylistService');
const config = require('./core/config');
const { Pool } = require('pg');
const ExportMailSender = require('./ExportMailSender');

const init = async () => {
  const pgPool = new Pool({
    user: config.postgres.user,
    password: config.postgres.password,
    host: config.postgres.host,
    database: config.postgres.db,
    port: config.postgres.port,
  });

  const playlistService = new PlaylistService(pgPool);

  const mailTransport = nodemailer.createTransport({
    host: config.smtp.host,
    port: config.smtp.port1,
    auth: {
      user: config.smtp.user,
      pass: config.smtp.password,
    },
  });
  const mailSender = new ExportMailSender(mailTransport);
  const exportConsumer = new ExportConsumer(playlistService, mailSender);
  const mqConnection = await amqp.connect(process.env.RABBITMQ_URI);
  const exportChannel = await mqConnection.createChannel();
  await exportChannel.assertQueue('export_mq', { durable: true });
  await exportChannel.consume('export_mq', exportConsumer.consume);
};

init();

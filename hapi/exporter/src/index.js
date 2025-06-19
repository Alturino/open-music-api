const { Pool } = require('pg');
const ExportConsumer = require('./ExportConsumer');
const PlaylistService = require('./PlaylistService');
const ExportMailSender = require('./ExportMailSender');
const amqp = require('amqplib');
const nodemailer = require('nodemailer');
const dotenv = require('dotenv');
const dotenvExpand = require('dotenv-expand');
dotenvExpand.expand(dotenv.config());
const config = require('./core/config');
const logger = require('./core/logger');

const init = async () => {
  const pgPool = new Pool({
    user: config.postgres.user,
    password: config.postgres.password,
    host: config.postgres.host,
    database: config.postgres.db,
    port: config.postgres.port,
  });

  logger.info('created PostgreSQL pool');

  logger.info('connecting to RabbitMQ');
  const mqConnection = await amqp.connect(config.rabbitmq.server);
  logger.info('connected to RabbitMQ');

  logger.info('creating RabbitMQ channel');
  const exportChannel = await mqConnection.createChannel();
  logger.info('created RabbitMQ channel');

  logger.info('asserting RabbitMQ queue');
  await exportChannel.assertQueue('export_mq', { durable: true });
  logger.info('asserted RabbitMQ queue');

  logger.info('creating nodemailer transport');
  const mailTransport = nodemailer.createTransport({
    host: config.smtp.host,
    port: config.smtp.port4,
    auth: {
      user: config.smtp.user,
      pass: config.smtp.password,
    },
  });
  logger.info('created nodemailer transport');
  const mailSender = new ExportMailSender(mailTransport);

  const playlistService = new PlaylistService(pgPool);
  const exportConsumer = new ExportConsumer(playlistService, mailSender);

  await exportChannel.consume('export_mq', exportConsumer.consume);
};

init();

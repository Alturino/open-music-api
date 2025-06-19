const config = {
  app: {
    host: process.env.HOST,
    port: process.env.PORT,
  },
  postgres: {
    user: process.env.POSTGRES_USER,
    password: process.env.POSTGRES_PASSWORD,
    host: process.env.POSTGRES_HOST,
    port: process.env.POSTGRES_PORT,
    db: process.env.POSTGRES_DB,
  },
  rabbitmq: {
    host: process.env.RABBITMQ_HOST,
    user: process.env.RABBITMQ_DEFAULT_USER,
    pass: process.env.RABBITMQ_DEFAULT_PASS,
    vhost: process.env.RABBITMQ_DEFAULT_VHOST,
    port: process.env.RABBITMQ_PORT,
    management_port: process.env.RABBITMQ_MANAGEMENT_PORT,
    server: process.env.RABBITMQ_SERVER,
  },
  smtp: {
    host: process.env.SMTP_HOST,
    port1: process.env.SMTP_PORT_1,
    port2: process.env.SMTP_PORT_2,
    port3: process.env.SMTP_PORT_3,
    port4: process.env.SMTP_PORT_4,
    user: process.env.SMTP_USER,
    password: process.env.SMTP_PASSWORD,
  },
};
module.exports = config;

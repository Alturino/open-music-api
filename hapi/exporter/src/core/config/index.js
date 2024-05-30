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
  smtp: {
    host: process.env.SMTP_HOST,
    port1: process.env.SMTP_PORT1,
    port2: process.env.SMTP_PORT2,
    port3: process.env.SMTP_PORT3,
    port4: process.env.SMTP_PORT4,
    user: process.env.SMTP_USER,
    password: process.env.SMTP_PASSWORD,
  },
};
module.exports = config;

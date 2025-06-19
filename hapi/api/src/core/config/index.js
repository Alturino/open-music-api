const dotenvExpand = require('dotenv-expand');
const dotenv = require('dotenv');
dotenvExpand.expand(dotenv.config());

const config = {
  app: {
    node_env: process.env.NODE_ENV,
    host: process.env.HOST,
    port: process.env.PORT,
    accessTokenKey: process.env.ACCESS_TOKEN_KEY,
    refreshTokenKey: process.env.REFRESH_TOKEN_KEY,
    accessTokenAge: process.env.ACCESS_TOKEN_AGE,
  },
  postgres: {
    user: process.env.PGUSER,
    password: process.env.PGPASSWORD,
    host: process.env.PGHOST,
    database: process.env.PGDATABASE,
    port: process.env.PGPORT,
  },
  rabbitmq: {
    host: process.env.RABBITMQ_SERVER,
    user: process.env.RABBITMQ_DEFAULT_USER,
    pass: process.env.RABBITMQ_DEFAULT_PASS,
    vhost: process.env.RABBITMQ_DEFAULT_VHOST,
    port: process.env.RABBITMQ_PORT,
    management_port: process.env.RABBITMQ_MANAGEMENT_PORT,
    server: process.env.RABBITMQ_SERVER,
  },
  redis: {
    host: process.env.REDIS_HOST,
    user: process.env.REDIS_USER,
    password: process.env.REDIS_PASSWORD,
    port: process.env.REDIS_PORT,
    server: process.env.REDIS_SERVER,
  },
};

module.exports = config;

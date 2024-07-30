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
    uri: process.env.RABBITMQ_URI,
  },
  redis: {
    host: process.env.REDIS_HOST,
    user: process.env.REDIS_USER,
    password: process.env.REDIS_PASSWORD,
    port: process.env.REDIS_PORT,
    url: process.env.REDIS_SERVER,
  },
};

module.exports = config;

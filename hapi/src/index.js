const AlbumService = require('./services/AlbumService');
const AlbumValidator = require('./validator/albums');
const ClientError = require('./exceptions/ClientError');
const Hapi = require('@hapi/hapi');
const SongService = require('./services/SongService');
const SongValidator = require('./validator/songs');
const albumsPlugin = require('./api/albums');
const songsPlugin = require('./api/songs');
const { Pool } = require('pg');
require('dotenv').config();

async function main() {
  const hapiServer = Hapi.server({
    host: process.env.HOST,
    port: process.env.PORT,
    routes: {
      cors: {
        origin: ['*'],
      },
    },
  });

  const pgPool = new Pool({
    user: process.env.POSTGRES_USER,
    password: process.env.POSTGRES_PASSWORD,
    host: process.env.POSTGRES_HOST,
    database: process.env.POSTGRES_DB,
    port: process.env.POSTGRES_PORT,
  });
  pgPool.connect((err) => {
    console.error(err);
  });

  const albumService = new AlbumService(pgPool);
  console.log('registering albums plugin');
  await hapiServer.register({
    plugin: albumsPlugin,
    options: {
      service: albumService,
      validator: AlbumValidator,
    },
  });
  console.log('albums plugin registered');

  const songService = new SongService(pgPool);
  console.log('registering songs plugin');
  await hapiServer.register({
    plugin: songsPlugin,
    options: {
      service: songService,
      validator: SongValidator,
    },
  });
  console.log('songs plugin registered');

  hapiServer.ext('onPreResponse', (request, h) => {
    // mendapatkan konteks response dari request
    const { response } = request;

    // penanganan client error secara internal.
    if (response instanceof ClientError) {
      return h
        .response({
          status: 'fail',
          message: response.message,
        })
        .code(response.statusCode);
    }
    if (response instanceof Error) {
      console.error(response);
    }

    return h.continue;
  });

  await hapiServer.start();
  console.log(`Server berjalan pada ${hapiServer.info.uri}`);
}

main();

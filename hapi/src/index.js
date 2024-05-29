const AlbumService = require('./albums/AlbumService');
const PlaylistService = require('./playlists/PlaylistService');
const AuthenticationService = require('./authentication/AuthenticationService');
const CollaborationsService = require('./collaborations/CollaborationsService');
const ExportService = require('./exports/ExportService');
const SongService = require('./songs/SongService');
const UserService = require('./users/UserService');

const AlbumValidator = require('./albums/validator');
const AuthentcationValidator = require('./authentication/validator');
const CollaborationValidator = require('./collaborations/validator');
const ExportValidator = require('./exports/validator');
const PlaylistValidator = require('./playlists/validator');
const SongValidator = require('./songs/validator');
const UserValidator = require('./users/validator');

const albumsPlugin = require('./albums/api');
const authenticationPlugin = require('./authentication/api');
const collaborationsPlugin = require('./collaborations/api');
const exportsPlugin = require('./exports/api');
const playlistPlugin = require('./playlists/api');
const songsPlugin = require('./songs/api');
const usersPlugin = require('./users/api');

const ClientError = require('./core/exceptions/ClientError');
const Hapi = require('@hapi/hapi');
const Jwt = require('@hapi/jwt');
const amqp = require('amqplib');
const TokenManager = require('./authentication/tokenize/TokenManager');
const config = require('./core/config');
const { Pool } = require('pg');

require('dotenv').config();

async function main() {
  const server = Hapi.server({
    host: config.app.host,
    port: config.app.port,
    routes: {
      cors: {
        origin: ['*'],
      },
    },
  });

  console.log('registering plugin jwt');
  await server.register([{ plugin: Jwt }]);
  console.log('plugin jwt registered');

  console.log('applying auth strategy jwt');
  server.auth.strategy('open_music_api_jwt', 'jwt', {
    keys: config.app.accessTokenKey,
    verify: {
      aud: false,
      iss: false,
      sub: false,
      maxAgeSec: config.app.accessTokenAge,
    },
    validate: (artifacts) => ({
      isValid: true,
      credentials: {
        id: artifacts.decoded.payload.id,
      },
    }),
  });
  console.log('auth strategy jwt applied');
  console.log('creating connection pool to postgres');
  const pgPool = new Pool({
    user: config.postgres.user,
    password: config.postgres.password,
    host: config.postgres.host,
    database: config.postgres.db,
    port: config.postgres.port,
  });
  pgPool.connect((err) => {
    if (err instanceof Error) {
      console.error(`main pgPool ${err}`);
    }
  });
  console.log('connection pool to postgres created');

  console.log('creating services');
  const albumService = new AlbumService(pgPool);
  const songService = new SongService(pgPool);
  const userService = new UserService(pgPool);
  const playlistService = new PlaylistService(pgPool);
  const rabbitmqConnection = await amqp.connect(config.rabbitmq.uri);
  const exportService = new ExportService(rabbitmqConnection);
  const collaborationsService = new CollaborationsService(pgPool);
  const authenticationService = new AuthenticationService(pgPool);
  console.log('services created');

  console.log('registering plugins');
  await server.register([
    {
      plugin: albumsPlugin,
      options: {
        service: albumService,
        validator: AlbumValidator,
      },
    },
    {
      plugin: songsPlugin,
      options: {
        service: songService,
        validator: SongValidator,
      },
    },
    {
      plugin: usersPlugin,
      options: {
        service: userService,
        validator: UserValidator,
      },
    },
    {
      plugin: authenticationPlugin,
      options: {
        authenticationService,
        userService,
        tokenManager: TokenManager,
        validator: AuthentcationValidator,
      },
    },
    {
      plugin: playlistPlugin,
      options: {
        service: playlistService,
        validator: PlaylistValidator,
      },
    },
    {
      plugin: collaborationsPlugin,
      options: {
        service: collaborationsService,
        validator: CollaborationValidator,
      },
    },
    {
      plugin: exportsPlugin,
      options: {
        service: exportService,
        validator: ExportValidator,
      },
    },
  ]);
  console.log('plugins registered');

  server.ext('onPreResponse', (request, h) => {
    const { response } = request;
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

  await server.start();
  console.log(`Server berjalan pada ${server.info.uri}`);
}

main();

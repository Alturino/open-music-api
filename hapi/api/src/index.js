const AlbumService = require('./albums/AlbumService');
const AlbumValidator = require('./albums/validator');
const albumPlugin = require('./albums/api');
const StorageService = require('./albums/StorageService');
const fs = require('fs');

const CollaborationsService = require('./collaborations/CollaborationsService');
const CollaborationValidator = require('./collaborations/validator');
const collaborationsPlugin = require('./collaborations/api');

const ExportService = require('./exports/ExportService');
const exportsPlugin = require('./exports/api');
const ExportValidator = require('./exports/validator');

const PlaylistService = require('./playlists/PlaylistService');
const PlaylistValidator = require('./playlists/validator');
const playlistPlugin = require('./playlists/api');

const UserService = require('./users/UserService');
const UserValidator = require('./users/validator');
const usersPlugin = require('./users/api');

const AuthentcationValidator = require('./authentication/validator');
const AuthenticationService = require('./authentication/AuthenticationService');
const TokenManager = require('./authentication/tokenize/TokenManager');
const authenticationPlugin = require('./authentication/api');

const SongService = require('./songs/SongService');
const SongValidator = require('./songs/validator');
const songsPlugin = require('./songs/api');

const Inert = require('@hapi/inert');

const ClientError = require('./core/exceptions/ClientError');
const Hapi = require('@hapi/hapi');
const Jwt = require('@hapi/jwt');
const amqp = require('amqplib');
const config = require('./core/config');
const { Pool } = require('pg');
const path = require('path');
const dotenvExpand = require('dotenv-expand');

dotenvExpand.expand(require('dotenv').config());

async function main() {
  const server = Hapi.server({
    debug: false,
    host: config.app.host,
    port: config.app.port,
    routes: {
      cors: {
        origin: ['*'],
      },
    },
  });

  await server.register([
    { plugin: Jwt },
    { plugin: Inert },
    {
      plugin: require('hapi-pino'),
      options: {
        level: 'debug',
        logPayload: true,
        logQueryParams: true,
        logPathParams: true,
        logRouteTags: true,
        logRequestStart: true,
        log4xxResponseErrors: true,
      },
    },
  ]);

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

  const pgPool = new Pool({
    user: config.postgres.user,
    password: config.postgres.password,
    host: config.postgres.host,
    database: config.postgres.db,
    port: config.postgres.port,
  });
  pgPool.connect((err) => {
    if (err instanceof Error) {
      server.logger.error(`main pgPool ${err}`);
    }
  });

  const staticFileFolder = path.resolve(__dirname, './public/assets');

  const albumService = new AlbumService(pgPool);
  const songService = new SongService(pgPool);
  const userService = new UserService(pgPool);
  const storageService = new StorageService(fs, staticFileFolder);
  const playlistService = new PlaylistService(pgPool);
  const rabbitmqConnection = await amqp.connect(config.rabbitmq.uri);
  const exportService = new ExportService(rabbitmqConnection);
  const collaborationsService = new CollaborationsService(pgPool);
  const authenticationService = new AuthenticationService(pgPool);

  await server.register([
    {
      plugin: albumPlugin,
      options: {
        albumService: albumService,
        storageService: storageService,
        albumValidator: AlbumValidator,
        folder: staticFileFolder,
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
        producerService: exportService,
        playlistService,
        validator: ExportValidator,
      },
    },
  ]);

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
      server.logger.error(response);
    }

    return h.continue;
  });

  await server.start();
}

main();

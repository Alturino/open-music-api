const logger = require('./core/logger');

class PlaylistService {
  constructor(pool) {
    this._pool = pool;

    this.getPlaylistToExport = this.getPlaylistToExport.bind(this);
  }

  async getPlaylistToExport(playlistId) {
    const queryPlaylist = {
      text: 'select p.id, p.name from playlists as p where p.id = $1;',
      values: [playlistId],
    };
    const queryPlaylistResult = await this._pool.query(queryPlaylist);
    const playlist = queryPlaylistResult.rows.map((row) => {
      return {
        id: row.id,
        name: row.name,
        username: row.username,
      };
    })[0];
    logger.info('got playlists', { playlist: playlist });

    const querySongs = {
      text: `
        select
          s.id,
          s.title,
          s.year,
          s.genre,
          s.performer,
          s.duration,
          s.album_id
        from playlists as p
        inner join playlists_and_songs as ps on p.id = ps.playlist_id
        inner join songs as s on ps.song_id = s.id
        where p.id = $1 and ps.playlist_id = $1;
      `,
      values: [playlistId],
    };
    const querySongsResult = await this._pool.query(querySongs);
    const songs = querySongsResult.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        performer: row.performer,
      };
    });
    logger.info('got songs', { songs: songs });

    const result = { ...playlist, songs };
    logger.info('got playlist and songs', { result: result });
    return result;
  }
}

module.exports = PlaylistService;

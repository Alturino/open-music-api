class PlaylistService {
  constructor(pool) {
    this._pool = pool;

    this.exportPlaylist = this.getPlaylistToExport.bind(this);
  }

  async getPlaylistToExport(playlistId, userId) {
    const queryPlaylist = {
      text: `
        select
            p.id,
            p.name,
        from playlists as p
        left join users as u on p.owner_id = u.id
        where p.id = $1;`,
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
        left join users as u on p.owner_id = u.id
        left join playlists_and_songs as ps on p.id = ps.playlist_id
        left join songs as s on ps.song_id = s.id`,
      values: [playlistId, userId],
    };
    const querySongsResult = await this._pool.query(querySongs);
    const songs = querySongsResult.rows.map((row) => {
      return {
        id: row.id,
        title: row.title,
        performer: row.performer,
      };
    });

    return {
      ...playlist,
      songs,
    };
  }
}

module.exports = PlaylistService;

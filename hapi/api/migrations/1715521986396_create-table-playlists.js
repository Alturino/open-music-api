/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.up = (pgm) => {
  pgm.createTable('playlists', {
    id: {
      type: 'varchar(16)',
      primaryKey: true,
      notNull: true,
    },
    name: {
      type: 'text',
      notNull: false,
    },
    owner_id: {
      type: 'varchar(16)',
      references: '"users"',
      onDelete: 'CASCADE',
      notNull: false,
    },
  });
  pgm.createTable('playlists_and_songs', {
    playlist_id: {
      type: 'varchar(16)',
      references: 'playlists',
      onDelete: 'CASCADE',
      notNull: true,
    },
    song_id: {
      type: 'varchar(16)',
      references: 'songs',
      onDelete: 'CASCADE',
      notNull: true,
    },
  });
};

/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.down = (pgm) => {
  pgm.dropTable('playlists');
  pgm.dropTable('playlists_and_songs');
};

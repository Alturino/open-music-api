/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.up = (pgm) => {
  pgm.createType('action', ['add', 'delete']);
  pgm.createTable('playlist_activities', {
    id: {
      type: 'varchar(16)',
      primaryKey: true,
      notNull: true,
    },
    playlist_id: {
      type: 'varchar(16)',
      references: '"playlists"',
      onDelete: 'CASCADE',
      notNull: true,
    },
    user_id: {
      type: 'varchar(16)',
      notNull: true,
    },
    action: {
      type: 'action',
      notNull: true,
    },
    song_id: {
      type: 'varchar(16)',
      notNull: false,
    },
    time: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
  });
};

/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.down = (pgm) => {
  pgm.dropType('action');
  pgm.dropTable('playlist_activities');
};

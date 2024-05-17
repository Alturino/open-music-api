/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.up = (pgm) => {
  pgm.createTable('collaborations', {
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
      references: '"users"',
      onDelete: 'CASCADE',
      notNull: false,
    },
  });
};

/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.down = (pgm) => {
  pgm.dropTable('collaborations');
};

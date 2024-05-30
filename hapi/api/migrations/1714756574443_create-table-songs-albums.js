/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.up = (pgm) => {
  pgm.createTable('albums', {
    id: {
      type: 'varchar(16)',
      primaryKey: true,
      notNull: true,
    },
    name: {
      type: 'varchar(50)',
      notNull: true,
    },
    year: {
      type: 'int',
      notNull: true,
    },
    created_at: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
    updated_at: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
    deleted_at: {
      type: 'timestamp',
      notNull: false,
      default: null,
    },
  });
  pgm.createTable('songs', {
    id: {
      type: 'varchar(16)',
      primaryKey: true,
      notNull: true,
    },
    title: {
      type: 'varchar(50)',
      notNull: true,
    },
    year: {
      type: 'int',
      notNull: true,
    },
    genre: {
      type: 'varchar(50)',
      notNull: true,
    },
    performer: {
      type: 'varchar(50)',
      notNull: true,
    },
    duration: {
      type: 'int',
      notNull: true,
    },
    album_id: {
      type: 'varchar(16)',
      references: '"albums"',
      onDelete: 'CASCADE',
      notNull: false,
    },
    created_at: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
    updated_at: {
      type: 'timestamp',
      notNull: true,
      default: pgm.func('current_timestamp'),
    },
    deleted_at: {
      type: 'timestamp',
      notNull: false,
      default: null,
    },
  });
};

/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.down = (pgm) => {
  pgm.dropTable('albums');
  pgm.dropTable('songs');
};

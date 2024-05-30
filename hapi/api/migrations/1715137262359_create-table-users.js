/**
 * @param pgm {import('node-pg-migrate').MigrationBuilder}
 * @param run {() => void | undefined}
 * @returns {Promise<void> | void}
 */
exports.up = (pgm) => {
  pgm.createTable('users', {
    id: {
      type: 'varchar(16)',
      primaryKey: true,
      notNull: true,
    },
    username: {
      type: 'varchar(50)',
      unique: true,
      notNull: true,
    },
    password: {
      type: 'text',
      notNull: true,
    },
    fullname: {
      type: 'varchar(50)',
      notNull: true,
    },
    refresh_token: {
      type: 'text',
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
  pgm.dropTable('users');
};

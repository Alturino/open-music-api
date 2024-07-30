const NotFoundError = require('../core/exceptions/NotFoundError');

class CacheService {
  constructor(redis) {
    this._redis = redis;

    this.get = this.get.bind(this);
    this.set = this.set.bind(this);
    this.delete = this.delete.bind(this);
  }

  async set(key, value, expirationInSecond = 3600) {
    await this._redis.set(key, value, { EX: expirationInSecond });
  }

  async get(key) {
    const res = await this._redis.get(key);
    if (res === null) {
      throw new NotFoundError('Cache tidak ditemukan');
    }
    return res;
  }

  async delete(key) {
    return await this._redis.del(key);
  }
}

module.exports = CacheService;

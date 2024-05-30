class StorageService {
  constructor(fs, folder) {
    this._fs = fs;
    this._folder = folder;

    if (!fs.existsSync(folder)) {
      fs.mkdirSync(folder, { recursive: true });
    }
  }

  writeFile(file, metadata) {
    const filename = `${new Date()}-${metadata.filename}`;
    const path = `${this._folder}/${filename}`;
    const filestream = this._fs.createWriteStream(path);

    return new Promise((resolve, reject) => {
      filestream.on('error', (e) => reject(e));
      file.pipe(filestream);
      file.on('end', () => resolve(filename));
    });
  }
}

module.exports = StorageService;

const http = require('http');
const fs = require('fs');
const path = require('path');
const os = require('os');
const { exec } = require('child_process');

const DEFAULT_PORT = 3000;

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
  '.txt': 'text/plain; charset=utf-8',
  '.sql': 'text/plain; charset=utf-8',
  '.java': 'text/plain; charset=utf-8'
};

function getLocalIpAddresses() {
  const interfaces = os.networkInterfaces();
  const addresses = [];
  for (const name of Object.keys(interfaces)) {
    for (const iface of interfaces[name]) {
      if (iface.family === 'IPv4' && !iface.internal) {
        addresses.push(iface.address);
      }
    }
  }
  return addresses;
}

const server = http.createServer((req, res) => {
  // CORS & Security Headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
  res.setHeader('X-Content-Type-Options', 'nosniff');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  let reqPath = req.url.split('?')[0];
  if (reqPath === '/' || reqPath === '') reqPath = '/index.html';

  // Sanitize path against directory traversal
  const safePath = path.normalize(decodeURIComponent(reqPath)).replace(/^(\.\.[\/\\])+/, '');
  const filePath = path.join(__dirname, safePath);

  fs.stat(filePath, (err, stats) => {
    if (err || !stats.isFile()) {
      res.writeHead(404, { 'Content-Type': 'text/html; charset=utf-8' });
      res.end(`
        <!DOCTYPE html>
        <html>
        <head><title>404 Not Found</title></head>
        <body style="font-family: sans-serif; text-align: center; padding: 50px;">
          <h2>404 - Halaman / Berkas Tidak Ditemukan</h2>
          <p>Berkas: <code>${reqPath}</code></p>
          <a href="/" style="color: #1765B0; font-weight: bold;">Kembali ke Halaman Utama SIAKAD</a>
        </body>
        </html>
      `);
      return;
    }

    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    fs.readFile(filePath, (readErr, content) => {
      if (readErr) {
        res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
        res.end('500 Internal Server Error: ' + readErr.message);
        return;
      }
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content);
    });
  });
});

function startServer(port) {
  server.listen(port, '0.0.0.0', () => {
    const localIps = getLocalIpAddresses();
    console.log('====================================================');
    console.log(' 🎓 [SIAKAD] Server Lokal Berhasil Dijalankan!');
    console.log('====================================================');
    console.log(` 💻 Akses Komputer (Localhost):`);
    console.log(`    http://localhost:${port}`);
    console.log(`    http://127.0.0.1:${port}`);
    console.log('');
    if (localIps.length > 0) {
      console.log(` 📱 Akses HP / Ponsel (Satu Jaringan Wi-Fi/Hotspot):`);
      localIps.forEach(ip => {
        console.log(`    http://${ip}:${port}`);
      });
      console.log('');
    }
    console.log(' 🔑 Kredensial Login: admin / admin');
    console.log(' Tekan Ctrl + C di terminal jika ingin menghentikan server.');
    console.log('====================================================');

    // Auto-open browser on localhost
    exec(`start http://localhost:${port}`, () => {});
  });

  server.on('error', (e) => {
    if (e.code === 'EADDRINUSE') {
      console.log(`⚠️ Port ${port} sedang digunakan, mencoba port ${port + 1}...`);
      startServer(port + 1);
    } else {
      console.error('❌ Gagal menjalankan server:', e);
    }
  });
}

startServer(DEFAULT_PORT);


<div align="center">

# 🌐 RemoteUniversalDevice

[![Version](https://img.shields.io/badge/version-2.0.0-blue?style=for-the-badge&logo=github)](https://github.com/dikaofc/RemoteUniversalDevice/releases)
[![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge&logo=apache)](LICENSE)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS%20%7C%20Windows%20%7C%20Linux-orange?style=for-the-badge&logo=linux)](https://github.com/dikaofc/RemoteUniversalDevice)
[![Stars](https://img.shields.io/github/stars/dikaofc/RemoteUniversalDevice?style=for-the-badge&color=yellow)](https://github.com/dikaofc/RemoteUniversalDevice/stargazers)

### 🔮 Kontrol Perangkat Universal dari Jarak Jauh dengan Teknologi Modern

[📖 Dokumentasi](#-dokumentasi) • [⚡ Fitur](#-fitur-utama) • [🚀 Instalasi](#-instalasi) • [💻 API Reference](#-api-reference) • [🤝 Kontribusi](#-kontribusi)

</div>

---

## 🎬 Demo Interaktif

<div align="center">

```html
<!-- 3D Device Preview Animation -->
<div style="position: relative; width: 300px; height: 200px; margin: 20px auto; perspective: 1000px;">
  <div style="position: absolute; width: 100%; height: 100%; transform-style: preserve-3d; animation: float 6s ease-in-out infinite;">
    <div style="position: absolute; width: 120px; height: 200px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 20px; left: 20px; transform: rotateY(-15deg) translateZ(50px); box-shadow: 0 20px 40px rgba(0,0,0,0.3);"></div>
    <div style="position: absolute; width: 120px; height: 200px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius: 20px; right: 20px; transform: rotateY(15deg) translateZ(50px); box-shadow: 0 20px 40px rgba(0,0,0,0.3);"></div>
    <div style="position: absolute; width: 100px; height: 160px; background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); border-radius: 15px; top: 20px; left: 100px; transform: translateZ(100px); box-shadow: 0 25px 50px rgba(0,0,0,0.4);"></div>
  </div>
  <style>
    @keyframes float {
      0%, 100% { transform: translateY(0px) rotateX(10deg); }
      50% { transform: translateY(-20px) rotateX(10deg); }
    }
  </style>
</div>
```

**🔗 Live Demo:** [https://dikaofc.github.io/RemoteUniversalDevice/demo](https://dikaofc.github.io/RemoteUniversalDevice/demo)

</div>

---

## ✨ Fitur Utama

<div align="center">

| 🎯 **Multi-Platform** | 🔒 **End-to-End Encryption** | ⚡ **Real-Time Sync** |
|:---:|:---:|:---:|
| Android, iOS, Windows, Linux, macOS | AES-256 + RSA 4096 | WebSocket + QUIC Protocol |

| 🖥️ **Screen Mirroring** | 📁 **File Transfer** | 🎮 **Remote Control** |
|:---:|:---:|:---:|
| 60fps Low Latency | 10GB/s Transfer Speed | Full Device Control |

</div>

### 🛠️ Kemampuan Teknis

```yaml
Core Features:
  - Remote Device Discovery: mDNS + UPnP + Bluetooth LE
  - Connection Protocol: WebSocket Secure (WSS) + TCP/UDP Hybrid
  - Authentication: OAuth 2.0 + JWT + Biometric
  - Encryption: AES-256-GCM + ChaCha20-Poly1305
  - Compression: Brotli + Zstandard
  - Logging: Structured JSON + ELK Stack Compatible

Supported Devices:
  - Android: 8.0+ (API 26+)
  - iOS: 14.0+
  - Windows: 10/11 (x64/ARM64)
  - Linux: Kernel 5.4+ (Ubuntu, Debian, Arch, Fedora)
  - macOS: 11.0+ (Big Sur+)
  - IoT: Raspberry Pi, Arduino ESP32, STM32
```

---

## 🚀 Instalasi

### 📦 Package Managers

<div align="center">

```bash
# npm / Node.js
npm install remote-universal-device

# pip / Python
pip install remote-universal-device

# Go
go get github.com/dikaofc/RemoteUniversalDevice

# Rust / Cargo
cargo install remote-universal-device

# Docker
docker pull dikaofc/remote-universal-device:latest
```

</div>

### 🔧 Manual Installation

#### Linux / macOS
```bash
# Clone repository
git clone https://github.com/dikaofc/RemoteUniversalDevice.git
cd RemoteUniversalDevice

# Install dependencies
chmod +x install.sh
./install.sh

# Build from source
make build

# Run daemon
sudo systemctl enable rud-daemon
sudo systemctl start rud-daemon
```

#### Windows PowerShell
```powershell
# Clone
git clone https://github.com/dikaofc/RemoteUniversalDevice.git
cd RemoteUniversalDevice

# Install with Chocolatey
choco install remote-universal-device

# Or use winget
winget install DikaOFC.RemoteUniversalDevice

# Run as Administrator
.\install.ps1
Start-Service RUDService
```

#### Android (Termux)
```bash
pkg update && pkg upgrade
pkg install python nodejs-lts git
git clone https://github.com/dikaofc/RemoteUniversalDevice
cd RemoteUniversalDevice
pip install -r requirements.txt
python main.py --mode=client
```

---

## 💻 Penggunaan Dasar

### 🎯 Mode Server (Host Device)

```python
from rud import RemoteUniversalDevice

# Inisialisasi Server
server = RemoteUniversalDevice(
    host="0.0.0.0",
    port=8443,
    ssl_cert="./certs/server.crt",
    ssl_key="./certs/server.key",
    encryption="AES-256-GCM"
)

# Konfigurasi Device
server.configure(
    device_name="My-Android-Phone",
    device_type="mobile",
    allow_screen_mirror=True,
    allow_file_transfer=True,
    max_connections=5
)

# Start Server
server.start()
print(f"🔗 Server running at: {server.get_qr_code_url()}")
```

### 📱 Mode Client (Controller)

```javascript
const RUD = require('remote-universal-device');

// Connect to Device
const client = new RUD.Client({
    serverUrl: "wss://192.168.1.100:8443",
    deviceId: "android-abc123",
    authToken: "your-jwt-token-here"
});

// Event Listeners
client.on('connected', () => {
    console.log('✅ Connected to device');
    client.screenMirror({ quality: 'high', fps: 60 });
});

client.on('screenFrame', (frame) => {
    // Render frame to canvas
    renderFrame(frame);
});

client.on('fileReceived', (file) => {
    console.log(`📁 Received: ${file.name} (${file.size} bytes)`);
});

// Control Commands
client.sendCommand('lock_screen');
client.sendCommand('take_screenshot', { format: 'png' });
client.sendCommand('install_app', { url: 'https://example.com/app.apk' });
```

### 🐳 Docker Quick Start

```bash
# Run Server Container
docker run -d \
  --name rud-server \
  -p 8443:8443 \
  -v ./certs:/app/certs \
  -v ./data:/app/data \
  -e RUD_MODE=server \
  -e RUD_ENCRYPTION_KEY="your-secure-key" \
  dikaofc/remote-universal-device:latest

# Run Client Container
docker run -it \
  -e RUD_MODE=client \
  -e RUD_SERVER_URL="wss://your-server:8443" \
  dikaofc/remote-universal-device:latest
```

---

## 🔐 Keamanan & Enkripsi

<div align="center">

```
┌─────────────────────────────────────────────────────────┐
│                    SECURITY ARCHITECTURE                  │
├─────────────────────────────────────────────────────────┤
│  🔒 Transport Layer: TLS 1.3 + Certificate Pinning       │
│  🔑 Key Exchange: ECDHE + RSA-4096                        │
│  🛡️  Data Encryption: AES-256-GCM / ChaCha20-Poly1305    │
│  📝 Integrity: HMAC-SHA256                                │
│  🚫 Anti-Tamper: Code Signing + Runtime Verification     │
│  🕵️  Audit Log: Immutable Ledger + SIEM Integration      │
└─────────────────────────────────────────────────────────┘
```

</div>

### Generate Certificates

```bash
# Generate CA
openssl genrsa -out ca.key 4096
openssl req -new -x509 -days 365 -key ca.key -out ca.crt

# Generate Server Cert
openssl genrsa -out server.key 4096
openssl req -new -key server.key -out server.csr
openssl x509 -req -days 365 -in server.csr -CA ca.crt -CAkey ca.key -out server.crt

# Generate Client Cert
openssl genrsa -out client.key 4096
openssl req -new -key client.key -out client.csr
openssl x509 -req -days 365 -in client.csr -CA ca.crt -CAkey ca.key -out client.crt
```

---

## 📊 API Reference

### REST API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/api/v1/auth/login` | Authenticate user | ❌ |
| `GET` | `/api/v1/devices` | List connected devices | ✅ |
| `POST` | `/api/v1/devices/{id}/connect` | Connect to device | ✅ |
| `GET` | `/api/v1/devices/{id}/screen` | Stream screen | ✅ |
| `POST` | `/api/v1/devices/{id}/command` | Execute command | ✅ |
| `GET` | `/api/v1/devices/{id}/files` | List files | ✅ |
| `POST` | `/api/v1/devices/{id}/files/upload` | Upload file | ✅ |
| `GET` | `/api/v1/logs` | Get audit logs | ✅ |

### WebSocket Events

```typescript
interface WebSocketEvents {
  // Connection
  'connect': { deviceId: string, session: string }
  'disconnect': { reason: string }
  
  // Screen
  'screen_frame': { data: Buffer, timestamp: number, resolution: [number, number] }
  'touch_event': { x: number, y: number, type: 'tap' | 'swipe' | 'long_press' }
  
  // File
  'file_progress': { filename: string, progress: number, speed: string }
  'file_complete': { filename: string, path: string, hash: string }
  
  // System
  'battery_status': { level: number, charging: boolean, temperature: number }
  'network_info': { type: 'wifi' | 'mobile', strength: number, ip: string }
  'app_list': { apps: Array<{name: string, package: string, version: string}> }
}
```

---

## 🎨 Dashboard UI

<div align="center">

![Dashboard Preview](https://via.placeholder.com/800x400/667eea/ffffff?text=RemoteUniversalDevice+Dashboard+Preview)

*Modern Glassmorphism Dashboard dengan Real-time Monitoring*

</div>

### Fitur Dashboard:
- 📊 **Real-time Metrics**: CPU, Memory, Network, Battery
- 🗺️ **Device Map**: Geolocation tracking dengan Leaflet.js
- 📹 **Live Screen**: Multi-device screen mirroring grid
- 📁 **File Manager**: Drag & drop file transfer
- ⌨️ **Remote Terminal**: SSH-like command execution
- 🔔 **Alert System**: Push notification integration

---

## 🧪 Testing

```bash
# Unit Tests
npm test
# or
pytest tests/ -v

# Integration Tests
npm run test:integration

# Performance Benchmark
npm run benchmark

# Security Audit
npm audit
# or
cargo audit

# Fuzzing
npm run fuzz
```

### Test Coverage

```
=================== COVERAGE REPORT ===================
File                    Stmts   Miss  Cover   Missing
-------------------------------------------------------
core/connection.py        245     12    95%   45-48, 102
core/encryption.py        189      5    97%   23
api/rest_handler.py       312     28    91%   145-150, 201
websocket/server.py       278     15    94%   89-92
-------------------------------------------------------
TOTAL                    1024     60    94%
=======================================================
```

---

## 🤝 Kontribusi

Kami menyambut kontribusi dari komunitas! 🎉

### 🚀 Quick Start Contributing

```bash
# Fork repository
git fork https://github.com/dikaofc/RemoteUniversalDevice

# Clone fork
git clone https://github.com/YOUR_USERNAME/RemoteUniversalDevice.git
cd RemoteUniversalDevice

# Create branch
git checkout -b feature/amazing-feature

# Make changes & commit
git commit -m "feat: add amazing feature"

# Push & PR
git push origin feature/amazing-feature
```

### 📋 Code Style

```yaml
Linting:
  - ESLint (JavaScript/TypeScript)
  - Black (Python)
  - gofmt (Go)
  - rustfmt (Rust)

Testing:
  - Jest / Vitest (JS/TS)
  - pytest (Python)
  - go test (Go)
  - cargo test (Rust)

Documentation:
  - JSDoc / TSDoc
  - Sphinx (Python)
  - GoDoc
  - rustdoc
```

---

## 📄 Lisensi

<div align="center">

```
MIT License

Copyright (c) 2024 DikaOFC

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

</div>

---

## 🙏 Ucapan Terima Kasih

Terima kasih kepada semua kontributor dan pendukung proyek ini! 🌟

<div align="center">

[![Contributors](https://contrib.rocks/image?repo=dikaofc/RemoteUniversalDevice)](https://github.com/dikaofc/RemoteUniversalDevice/graphs/contributors)

**Made with ❤️ by DikaOFC & Community**

</div>

---

## 📞 Kontak & Support

- 🐦 **Twitter**: [@dikaofc](https://twitter.com/dikaofc)
- 💬 **Discord**: [Join Server](https://discord.gg/dikaofc)
- 📧 **Email**: dika@ofc.dev
- 🐛 **Issues**: [GitHub Issues](https://github.com/dikaofc/RemoteUniversalDevice/issues)
- 💡 **Discussions**: [GitHub Discussions](https://github.com/dikaofc/RemoteUniversalDevice/discussions)

---

<div align="center">

### ⭐ Jika proyek ini membantu Anda, jangan lupa beri bintang! ⭐

[![Star History](https://api.star-history.com/svg?repos=dikaofc/RemoteUniversalDevice&type=Date)](https://star-history.com/#dikaofc/RemoteUniversalDevice&Date)

</div>

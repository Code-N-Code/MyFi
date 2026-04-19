# MyFi - Local Network File Sharing

![MyFi Icon](https://img.icons8.com/fluency/96/000000/wi-fi-connected.png)

MyFi is a lightweight Android application that transforms your smartphone into a local HTTP file server. It allows users to share files from their device to any browser on the same Wi-Fi network using the Storage Access Framework (SAF).

## Features

- Storage Access Framework (SAF): Securely browse and select directories without requiring broad storage permissions.
- High-Performance Streaming: Stream large files directly to the browser using buffered input streams.
- QR Code Integration: Quickly share the server URL via generated QR codes.
- Mustache Templating: Clean, dynamic HTML interface rendered on the fly.
- Router Architecture: Decoupled handler pattern for a scalable and maintainable server.

## Tech Stack

Based on the project's configuration (v8.13.2):

- Server: NanoHTTPD (v2.3.1)
- Templating: Mustache.java (v0.9.14)
- Dependency Injection: Hilt (v2.51.1)
- QR Generation: ZXing (v3.5.4)
- UI: Material 3 (v1.13.0) & AndroidX AppCompat (v1.7.1)
- Core: AndroidX Lifecycle (v2.10.0)

## Project Architecture

The project uses a Router/Handler pattern to separate server logic from routing:

com.codencode.myfi
├── filereader/         # Logic for SAF and directory scanning
├── server/             # NanoHTTPD Server implementation
│   ├── handlers/       # Individual route logic (Index, Stream, Download)
│   ├── RouteHandler    # Interface for all endpoints
│   └── FileServer      # The main traffic controller (Router)
└── ui/                 # Activity and Fragment logic

## Installation & Setup

1. Clone the repository:
   git clone https://github.com/yourusername/MyFi.git

2. Sync Project:
   Open in Android Studio (Hedgehog or newer). The project uses a Version Catalog (libs.versions.toml).

3. Required Permissions:
    - INTERNET: To host the local server.
    - ACCESS_WIFI_STATE: To determine the local IP address.

## License

Copyright 2024 Waqar Ahmad

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

---
Made with ❤️ for local sharing
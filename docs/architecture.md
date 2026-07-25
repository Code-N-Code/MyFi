# MyFi architecture

MyFi uses a feature-based package structure. A file belongs with the feature that
uses it; code is placed in `core` only when it can be shared by multiple features.

## Package ownership

```text
com.codencode.myfi
├── MainActivity
├── core/
│   ├── format/     File-size formatting and MIME-type resolution
│   ├── http/       The common NanoHTTPD endpoint contract
│   ├── io/         Streaming progress support
│   ├── network/    Local address lookup and QR-code generation
│   └── storage/    Generic document access helpers
└── feature/
    └── send/
        ├── data/   Android Storage Access Framework implementations
        ├── domain/ Immutable sharing and transfer models
        ├── server/ Local HTTP server and browser endpoints
        └── ui/     Android screen, ViewModel, and rendered state
```

## Send flow

1. `MainActivity` hosts `SendFragment`.
2. `SendFragment` sends user actions to `SendViewModel` and renders `SendUiState`.
3. `SendViewModel` delegates sharing work to `ShareSessionManager`.
4. `ShareSessionManager` selects files through `ShareFolderRepository` and starts or stops `ShareServer`.
5. `ShareServer` routes browser requests to the send endpoints.
6. Download progress flows back through the session manager and ViewModel to the fragment.

## Adding a new feature

For a receive feature, create `feature/receive` with the same focused packages when
they are needed:

```text
feature/receive/
├── data/       Where incoming files are stored
├── domain/     Incoming-transfer models and state
├── server/     Upload endpoints or transfer protocol handling
└── ui/         Receive screen and ViewModel
```

Do not place receive-specific code in `feature/send`. Move a class to `core` only
after it is genuinely useful to both features.

## Lifecycle rule

The current Send screen starts its local server when the screen opens and ends the
sharing session when the screen is destroyed. Background sharing, folder restoration,
and persistent transfer history are intentional future features rather than implicit
side effects of the current architecture.

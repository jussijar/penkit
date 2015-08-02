# penkit

## Compile & Run

```bash
  $ npm install
  $ lein compile

  # Start app
  $ node target/server.js

  # Open http://localhost:3000/
```

## Development

```bash
  # Install nodemon if not installed already
  $ npm install nodemon -g
  
  # Auto compile changed script files
  $ lein cljsbuild auto

  # Use nodemon to restart server on changes
  $ nodemon target/server.js

  # Open http://localhost:3000/
```
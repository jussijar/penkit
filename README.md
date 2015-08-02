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

## Heroku checklist

Insert envvars such as mongouri

Insert buildpacks
See https://devcenter.heroku.com/articles/buildpacks#buildpack-detect-order

Insert procfile

Insert min-version for leiningen

If all else fails
```
heroku run bash 
```


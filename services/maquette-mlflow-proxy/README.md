# Maquette MLflow Proxy

The MLflow proxy ensures that requests from Maquette UI are routed to the correct MLflow server instance. And it ensures the authorization of the access.

## Get Started

To start the proxy run the following commands:

```
$ npm install
$ PORT=3040 npm run start
```

The `PORT` is optional and can also be set within the `service/config.json`.

## What it does

![Maquette Authentication Proxy](./maquette-authentication-proxy.png)

> TODO

## Configuration 

You may configure additional settings in `service/config.json`. The following configurations are available.

* **port** - The port for the proxy server to listen. Can be overriden by environemnt variable `PORT`.

## API Endpoints

`POST /api/routes`

Register or update a route.

```bash
$ curl \
    --request POST 'http://localhost:3040/api/routes' \
    --header 'Content-Type: application/json' \
    --data-raw '{"id": "af10ccfc40", "route": "/_mlflow/af10ccfc40", target: "http://localhost:5720"}'
```

`DELETE /api/routes`

Unregister or delete information about the routes.

```bash
$ curl \
    --request DELETE 'http://localhost:3040/api/routes/:id'
```

`GET /api/routes`

List registered routes.

```bash
$ curl \
    --request GET 'http://localhost:3040/api/routes'
```

## Deployment

For deployment purposes (e.g. for local testing, the service can be packaged in a Docker file):

```bash
docker build -t mq-services--mlflow-proxy:0.0.1 .
```
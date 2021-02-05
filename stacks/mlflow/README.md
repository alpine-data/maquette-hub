# MLflow Stack

## Build containers for Maquette

Maquette requires the Docker images to be present in local image repository.

```bash
$ docker build -t mq-stacks--mlflow-minio:0.0.1 -f ./minio/Dockerfile ./minio
$ docker build -t mq-stacks--mlflow-server:0.0.1 -f ./mlflow/Dockerfile ./mlflow
```

## Docker Compose Setup

The stack can be tested with Docker Compose. 

```bash
$ docker-compose up -d
```

The MLflow server is configured to publish models to the minio object storage into the bucket `mlflow` (see `docker-compose.yaml`). Before starting with MLflow projects, this bucket must be created manually (e.g. via minio [Web UI](http://localhost:9000)).
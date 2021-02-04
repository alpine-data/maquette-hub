# MLflow Stack

## Docker Compose Setup

The stack can be tested with Docker Compose. 

```bash
$ docker-compose up -d
```

The MLflow server is configured to publish models to the minio object storage into the bucket `mlflow` (see `docker-compose.yaml`). Before starting with MLflow projects, this bucket must be created manually (e.g. via minio [Web UI](http://localhost:9000)).
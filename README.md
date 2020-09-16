# maquette plattform

## Issues

### Direct communication from training runtime to MLflow's artifact store

Currently MLflow clients need direct access to the server's S3 storage, thus URL and credentials of the server need to be configured locally. There is a discussion within the community to provide a proxy API to upload artifacts w/o direct access (https://github.com/mlflow/mlflow/issues/629). But as of now this requires workarounds to simplify its usage.

* Using presigned URL? (https://docs.min.io/docs/upload-files-from-browser-using-pre-signed-urls.html)
FROM minio/minio:RELEASE.2020-12-12T08-39-07Z

EXPOSE 9000
RUN mkdir -p /data/mlflow
ENTRYPOINT []
CMD ["/usr/bin/minio", "server", "/data"]
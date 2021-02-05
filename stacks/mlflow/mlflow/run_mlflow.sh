#!/bin/bash

while true
do
   mlflow server --backend-store-uri postgresql://${POSTGRES_USERNAME}:${POSTGRES_PASSWORD}@${POSTGRES_HOST}:5432/postgres --default-artifact-root s3://mlflow/ --host 0.0.0.0 --static-prefix ${MLFLOW_PREFIX}
   
   "MLflow will be restarted in 15 seconds ..."
   sleep 15
done

FROM python:3.7-slim-buster

ADD run_mlflow.sh /opt/run_mlflow.sh
RUN \
    pip install mlflow boto3 psycopg2-binary && \
    chmod +x /opt/run_mlflow.sh

CMD /opt/run_mlflow.sh
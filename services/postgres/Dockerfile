FROM postgres:latest

COPY samples /var/lib/postgresql/data/samples
COPY init/init-samples.sh /docker-entrypoint-initdb.d/init-samples.sh

RUN chmod +x /docker-entrypoint-initdb.d/init-samples.sh

#!/bin/bash

set -e

psql --username "${POSTGRES_USER}" --dbname "${POSTGRES_DB}" <<-EOSQL
    CREATE DATABASE sample_legodwh
EOSQL

psql \
    --username "${POSTGRES_USER}" \
    --dbname "sample_legodwh" \
    -a -f /var/lib/postgresql/data/samples/legodwh.sql
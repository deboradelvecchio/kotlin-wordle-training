#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --echo-all <<-EOSQL
	CREATE USER db_user WITH PASSWORD 'db_pass';
	CREATE DATABASE "kotlin-wordle-training-dev";
	GRANT ALL PRIVILEGES ON DATABASE "kotlin-wordle-training-dev" TO db_user;
	\c kotlin-wordle-training-dev
	GRANT CREATE ON SCHEMA public TO db_user;
EOSQL
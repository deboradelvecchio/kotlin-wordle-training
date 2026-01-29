#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --echo-all <<-EOSQL
	CREATE USER ${DATABASE_USER:-db_user} WITH PASSWORD '${DATABASE_PASSWORD:-db_pass}';
	CREATE DATABASE "${DATABASE_NAME:-kotlin-wordle-training-dev}";
	GRANT ALL PRIVILEGES ON DATABASE "${DATABASE_NAME:-kotlin-wordle-training-dev}" TO ${DATABASE_USER:-db_user};
	\c "${DATABASE_NAME:-kotlin-wordle-training-dev}"
	GRANT CREATE ON SCHEMA public TO ${DATABASE_USER:-db_user};
EOSQL
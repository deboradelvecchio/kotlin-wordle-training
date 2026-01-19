#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" --echo-all <<-EOSQL
	CREATE USER ${DB_USER:-db_user} WITH PASSWORD '${DB_PASSWORD:-db_pass}';
	CREATE DATABASE "${DATABASE_NAME:-kotlin-wordle-training-dev}";
	GRANT ALL PRIVILEGES ON DATABASE "${DATABASE_NAME:-kotlin-wordle-training-dev}" TO ${DB_USER:-db_user};
	\c "${DATABASE_NAME:-kotlin-wordle-training-dev}"
	GRANT CREATE ON SCHEMA public TO ${DB_USER:-db_user};
EOSQL
CREATE ROLE vintage WITH LOGIN PASSWORD 'vintage';
CREATE ROLE contosostore WITH LOGIN PASSWORD 'contosostore' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE contosostore_database;
GRANT ALL PRIVILEGES ON DATABASE contosostore_database TO contosostore;
GRANT ALL PRIVILEGES ON DATABASE contosostore_database TO vintage;

-- Connect to the contosostore_database and grant schema permissions
\c contosostore_database;
GRANT ALL ON SCHEMA public TO contosostore;
GRANT ALL ON SCHEMA public TO vintage;

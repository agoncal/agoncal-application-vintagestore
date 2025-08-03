CREATE ROLE vintage WITH LOGIN PASSWORD 'vintage';
CREATE ROLE vintagestore WITH LOGIN PASSWORD 'vintagestore' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;

CREATE DATABASE vintagestore_database;
GRANT ALL PRIVILEGES ON DATABASE vintagestore_database TO vintagestore;
GRANT ALL PRIVILEGES ON DATABASE vintagestore_database TO vintage;

-- Connect to the vintagestore_database and grant schema permissions
\c vintagestore_database;
GRANT ALL ON SCHEMA public TO vintagestore;
GRANT ALL ON SCHEMA public TO vintage;

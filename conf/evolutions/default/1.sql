# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE users (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(255) NOT NULL
);

# --- Insertions

INSERT INTO users (name) VALUES ('Sven');

 
# --- !Downs
 
DROP TABLE IF EXISTS users;

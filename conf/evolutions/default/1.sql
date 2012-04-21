# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups
 
CREATE TABLE users (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(255) NOT NULL
);

INSERT INTO users (name) VALUES ('Sven');

 
# --- !Downs
 
DROP TABLE IF EXISTS users;

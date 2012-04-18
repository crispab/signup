# --- First database schema
 
# --- !Ups
 
CREATE TABLE users (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(255) NOT NULL
);

INSERT INTO users SET name='Sven';
 
# --- !Downs
 
DROP TABLE IF EXISTS users;

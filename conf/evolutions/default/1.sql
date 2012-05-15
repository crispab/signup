# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE users (
  id                        SERIAL PRIMARY KEY,
  first_name                VARCHAR(127) NOT NULL,
  nick_name 				VARCHAR(127) DEFAULT '',
  last_name 				VARCHAR(127) DEFAULT '',
  primary_email 			VARCHAR(127) DEFAULT '',
  secondary_email 			VARCHAR(127) DEFAULT '',
  mobile_nr 				VARCHAR(31) DEFAULT '',
  comment 					text default ''
);

INSERT INTO users (id, first_name, comment) VALUES (-1, 'Fredrik', 'En glad statsminister');
INSERT INTO users (id, first_name, last_name) VALUES (-2, 'Torbjörn', 'Fälldin');
INSERT INTO users (id, first_name, last_name, comment) VALUES (-3, 'Göran', 'Persson', 'En f.d. statsminister');
INSERT INTO users (id, first_name, last_name, comment) VALUES (-4, 'Frodo', 'Baggins', 'Ringbärare');

# --- !Downs
 
DROP TABLE IF EXISTS users;

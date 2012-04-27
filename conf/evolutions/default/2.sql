# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE events (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(127) NOT NULL,
  description 				VARCHAR(127) DEFAULT '',
  when  			        TIMESTAMP NOT NULL
);

--INSERT INTO users (first_name) VALUES ('Fredrik');
--INSERT INTO users (first_name, last_name) VALUES ('Göran', 'Persson');

# --- !Downs
 
DROP TABLE IF EXISTS events;

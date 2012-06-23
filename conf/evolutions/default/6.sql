# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE groups (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(127) NOT NULL,
  description               VARCHAR(127) DEFAULT ''
);

INSERT INTO groups (id, name, description) VALUES (-1, 'Crisp Rocket Days', 'För dej som vill lära dej mer');
INSERT INTO groups (id, name, description) VALUES (-2, 'Näsknäckarna', 'Innebandylaget för hårdingar');

# --- !Downs
 
DROP TABLE IF EXISTS groups;

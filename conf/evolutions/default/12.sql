# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE users ADD COLUMN permission VARCHAR(20) DEFAULT 'NormalUser' NOT NULL;
ALTER TABLE users ADD COLUMN pwd VARCHAR(127) DEFAULT '*';


# --- !Downs

ALTER TABLE users DROP COLUMN pwd;
ALTER TABLE users DROP COLUMN permission;


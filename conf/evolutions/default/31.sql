# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups

ALTER TABLE users ADD COLUMN provider_key VARCHAR(32);
ALTER TABLE users ADD COLUMN auth_info text;

# --- !Downs

ALTER TABLE users DROP COLUMN provider_key;
ALTER TABLE users DROP COLUMN auth_info;




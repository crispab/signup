# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups

ALTER TABLE events ADD COLUMN event_type VARCHAR(32) NOT NULL DEFAULT 'NormalEvent';

# --- !Downs

ALTER TABLE users DROP COLUMN event_type;


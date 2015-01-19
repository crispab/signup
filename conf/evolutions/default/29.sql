# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups

ALTER TABLE events ADD COLUMN max_participants INTEGER;

# --- !Downs

ALTER TABLE events DROP COLUMN max_participants;


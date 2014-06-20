# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE events ADD COLUMN allow_extra_friends BOOLEAN DEFAULT FALSE NOT NULL;

# --- !Downs

ALTER TABLE events DROP COLUMN allow_extra_friends;

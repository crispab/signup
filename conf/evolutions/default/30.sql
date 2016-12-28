# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups

ALTER TABLE events ADD COLUMN cancellation_reason VARCHAR(512);

# --- !Downs

ALTER TABLE events DROP COLUMN cancellation_reason;


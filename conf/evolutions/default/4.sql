# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE events ADD COLUMN start_time TIMESTAMP;
UPDATE events SET start_time=whenx;
ALTER TABLE events ALTER COLUMN start_time SET NOT NULL;

ALTER TABLE events ADD COLUMN end_time TIMESTAMP;
UPDATE events SET end_time=whenx;
ALTER TABLE events ALTER COLUMN end_time SET NOT NULL;

ALTER TABLE events DROP COLUMN whenx;

# --- !Downs
 
ALTER TABLE events ADD COLUMN whenx TIMESTAMP;
UPDATE events SET whenx=start_time;
ALTER TABLE events ALTER COLUMN whenx SET NOT NULL;

ALTER TABLE events DROP COLUMN start_time;
ALTER TABLE events DROP COLUMN end_time;

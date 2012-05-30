# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
ALTER TABLE events RENAME COLUMN whenx TO start_time;

ALTER TABLE events ADD COLUMN end_time TIMESTAMP;
UPDATE events SET end_time=start_time;
ALTER TABLE events ALTER COLUMN end_time SET NOT NULL;


# --- !Downs
 
ALTER TABLE events DROP COLUMN IF EXISTS end_time;
ALTER TABLE events RENAME COLUMN start_time TO whenx;

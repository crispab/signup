# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups

ALTER TABLE events RENAME COLUMN event_type TO event_status;
ALTER TABLE events ALTER COLUMN event_status SET DEFAULT 'Created';

UPDATE events SET event_status = 'Created' WHERE event_status = 'NormalEvent';

# --- !Downs

ALTER TABLE events RENAME COLUMN event_status TO event_type;
ALTER TABLE events ALTER COLUMN event_type SET DEFAULT 'NormalEvent';

UPDATE events SET event_type='NormalEvent' WHERE event_type = 'Created';


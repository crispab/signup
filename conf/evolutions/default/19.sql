# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE events ADD COLUMN last_signup_date TIMESTAMP;
UPDATE events SET last_signup_date=start_time;
ALTER TABLE events ALTER COLUMN last_signup_date SET NOT NULL;

# --- !Downs

ALTER TABLE events DROP COLUMN last_signup_date;

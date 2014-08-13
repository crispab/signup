# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE participations ADD COLUMN signup_time TIMESTAMP;

# --- !Downs

ALTER TABLE participations DROP COLUMN signup_time;

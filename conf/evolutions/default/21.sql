# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE events ALTER COLUMN last_signup_date TYPE DATE;

ALTER TABLE reminders ADD COLUMN datex DATE;
UPDATE reminders SET datex = cast((SELECT last_signup_date FROM events WHERE reminders.event = events.id) AS DATE) - reminders.days_before;
ALTER TABLE reminders ALTER COLUMN datex SET NOT NULL;

ALTER TABLE reminders DROP COLUMN days_before;

# --- !Downs

ALTER TABLE reminders ADD COLUMN days_before INTEGER;
UPDATE reminders SET days_before = cast((SELECT last_signup_date FROM events WHERE reminders.event = events.id) AS DATE) - reminders.datex;
ALTER TABLE reminders ALTER COLUMN days_before SET NOT NULL;

ALTER TABLE reminders DROP COLUMN datex;

ALTER TABLE events ALTER COLUMN last_signup_date TYPE TIMESTAMP;

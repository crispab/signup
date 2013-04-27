# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

CREATE TABLE reminders (
  id                        SERIAL PRIMARY KEY,
  days_before               INTEGER NOT NULL,
  event                     INTEGER,
  FOREIGN KEY(event) REFERENCES events(id)
);

# --- !Downs

DROP TABLE IF EXISTS reminders;

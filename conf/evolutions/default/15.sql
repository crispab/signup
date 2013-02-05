# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

CREATE TABLE log_entries (
  id                        SERIAL PRIMARY KEY,
  event                     INTEGER,
  message                   VARCHAR(512) NOT NULL,
  whenx                     TIMESTAMP NOT NULL,
  FOREIGN KEY(event) REFERENCES events(id)
);


# --- !Downs

DROP TABLE IF EXISTS log_entries;

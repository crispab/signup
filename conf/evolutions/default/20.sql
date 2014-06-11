# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE events ALTER COLUMN description TYPE VARCHAR(10240);

# --- !Downs

ALTER TABLE events ALTER COLUMN description TYPE VARCHAR(512);


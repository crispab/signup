# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/

# --- !Ups
ALTER TABLE events ALTER last_signup_date TYPE TIMESTAMP;

# --- !Downs

ALTER TABLE events ALTER last_signup_date TYPE date;




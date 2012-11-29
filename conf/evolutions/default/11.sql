# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

UPDATE users set email=lower(first_name) || '.' || lower(last_name) || '@crisp.se' where email='unknown@crisp.se';

ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email);


# --- !Downs

ALTER TABLE users DROP CONSTRAINT unique_email;


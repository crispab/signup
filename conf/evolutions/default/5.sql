# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

UPDATE users SET last_name='Unknown' where last_name IS NULL or last_name='';
ALTER TABLE users ALTER COLUMN last_name SET NOT NULL;

ALTER TABLE users ADD COLUMN email VARCHAR(127);
UPDATE users SET email=primary_email;
ALTER TABLE users DROP COLUMN primary_email;

UPDATE users SET email='unknown@crisp.se' where email IS NULL OR email = '';
ALTER TABLE users ALTER COLUMN email SET NOT NULL;

ALTER TABLE users ADD COLUMN phone VARCHAR(127) DEFAULT '';
UPDATE users SET phone=mobile_nr;
ALTER TABLE users DROP COLUMN mobile_nr;

ALTER TABLE users DROP COLUMN nick_name;
ALTER TABLE users DROP COLUMN secondary_email;

# --- !Downs

ALTER TABLE users ADD COLUMN primary_email VARCHAR(127) DEFAULT '';
UPDATE users SET primary_email = email;
ALTER TABLE users DROP COLUMN email;

ALTER TABLE users ADD COLUMN mobile_nr VARCHAR(127) DEFAULT '';
UPDATE users SET mobile_nr=phone;
ALTER TABLE users DROP COLUMN phone;

ALTER TABLE users ADD COLUMN nick_name VARCHAR(127) DEFAULT '';
ALTER TABLE users ADD COLUMN secondary_email VARCHAR(127) DEFAULT '';

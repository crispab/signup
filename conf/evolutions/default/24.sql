# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

-- For H2 that doesn't have a built in md5 function
--CREATE ALIAS md5 FOR "org.apache.commons.codec.digest.DigestUtils.md5Hex(java.lang.String)";

ALTER TABLE users ADD COLUMN image_url VARCHAR(2500);
UPDATE users SET image_url='https://secure.gravatar.com/avatar/' || md5(lower(email)) ||'.jpg?default=mm&size={size}';
ALTER TABLE users ALTER COLUMN image_url SET NOT NULL;

# --- !Downs

ALTER TABLE users DROP COLUMN image_url;

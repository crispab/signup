# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

ALTER TABLE users ADD COLUMN image_version VARCHAR(32);
ALTER TABLE users RENAME COLUMN image_url TO image_provider;

UPDATE users SET image_provider = 'Cloudinary' WHERE image_provider LIKE '%cloudinary.com%';
UPDATE users SET image_provider = 'Gravatar' WHERE image_provider != 'Cloudinary';

# --- !Downs

ALTER TABLE users RENAME COLUMN image_provider TO image_url;
ALTER TABLE users DROP COLUMN image_version;

UPDATE users SET image_url='https://secure.gravatar.com/avatar/' || md5(lower(email)) ||'.jpg?default=mm&size={size}';
-- No way to restore Cloudinary URLs here. Have to fix it manually using the application.


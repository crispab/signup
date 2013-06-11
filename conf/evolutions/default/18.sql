# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally
# ---
# --- Password is 'admin'

# --- !Ups

ALTER TABLE groups ADD COLUMN mail_subject_prefix VARCHAR(127) DEFAULT '' NOT NULL;
UPDATE groups SET mail_subject_prefix=name;

# --- !Downs

ALTER TABLE groups DROP COLUMN mail_subject_prefix;

# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

INSERT INTO users (id, first_name, last_name, comment, email) VALUES (-5,'John', 'Doe', 'Okändis', 'john@doe.net');
INSERT INTO participations (id, status, comment, userx, event) VALUES (-5, 'On', 'Nice!', -5, -1);

# --- !Downs
 
DELETE FROM users WHERE id = -5;
DELETE FROM participations WHERE id = -5;

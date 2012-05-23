# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE events (
  id                        SERIAL PRIMARY KEY,
  name                      VARCHAR(127) NOT NULL,
  description               VARCHAR(127) DEFAULT '',
  venue                     VARCHAR(127) DEFAULT '',
  whenx                     TIMESTAMP NOT NULL
);

INSERT INTO events (id, name, description,whenx,venue) VALUES (-1, 'Crisp RD', 'Vad jag lärde mig av BigFamilyTrip', TIMESTAMP '2012-05-03 18:00:00', 'Crisp Office');
INSERT INTO events (id, name, description,whenx,venue) VALUES (-2, 'Crisp RD', 'Scala 3.0 och Play 3.0', TIMESTAMP '2013-05-03 18:00:00', 'Crisp Office');
INSERT INTO events (id, name, description,whenx,venue) VALUES (-3, 'Julbord', 'Hej tomtegubbar...', TIMESTAMP '2012-12-20 17:30:00', 'Crisp Office');

# --- !Downs
 
DROP TABLE IF EXISTS events;

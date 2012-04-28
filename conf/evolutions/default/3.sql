# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE participations (
  id                        SERIAL PRIMARY KEY,
  status                    VARCHAR(20) NOT NULL,
  comment 		    VARCHAR(127) DEFAULT '',
  user                      INTEGER,
  event                     INTEGER,
  FOREIGN KEY(user) REFERENCES users(id),
  FOREIGN KEY(event) REFERENCES events(id)
);


# --- !Downs
 
DROP TABLE IF EXISTS participations;

# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE participations (
  id                        SERIAL PRIMARY KEY,
  status                    VARCHAR(20) NOT NULL,
  comment 		    VARCHAR(127) DEFAULT '',
  userx                      INTEGER,
  event                     INTEGER,
  FOREIGN KEY(userx) REFERENCES users(id),
  FOREIGN KEY(event) REFERENCES events(id)
);

INSERT INTO participations (status, comment, userx, event) VALUES ('On', 'Trevligt!', -1, -1);


# --- !Downs
 
DROP TABLE IF EXISTS participations;

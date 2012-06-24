# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
CREATE TABLE memberships (
  id                        SERIAL PRIMARY KEY,
  userx                     INTEGER NOT NULL,
  groupx                    INTEGER NOT NULL,
  FOREIGN KEY(userx)        REFERENCES users(id),
  FOREIGN KEY(groupx)       REFERENCES groups(id)
);

INSERT INTO memberships (id, userx, groupx) VALUES (-1, -1, -1);
INSERT INTO memberships (id, userx, groupx) VALUES (-2, -2, -1);
INSERT INTO memberships (id, userx, groupx) VALUES (-3, -3, -2);
INSERT INTO memberships (id, userx, groupx) VALUES (-4, -4, -2);


# --- !Downs
 
DROP TABLE IF EXISTS memberships;

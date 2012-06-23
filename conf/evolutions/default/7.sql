# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups
 
ALTER TABLE events ADD COLUMN groupx INTEGER;
ALTER TABLE events ADD CONSTRAINT fk1 FOREIGN KEY(groupx) REFERENCES groups(id);

UPDATE events SET groupx = -1 WHERE name='Crisp RD';
UPDATE events SET groupx = -2 WHERE groupx IS NULL;

ALTER TABLE events ALTER COLUMN groupx SET NOT NULL;


# --- !Downs

ALTER TABLE events DROP COLUMN groupx;

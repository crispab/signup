# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally

# --- !Ups

DELETE FROM participations WHERE id IN (
  SELECT min(id) AS oldest_id FROM participations WHERE (userx, event) IN (
    SELECT userx, event FROM (
      SELECT userx, event, count(*) AS duplicates FROM participations GROUP BY userx, event
    ) AS foo WHERE duplicates > 1
  ) GROUP BY userx, event
);

ALTER TABLE participations ADD CONSTRAINT unique_user_event UNIQUE (userx, event);


# --- !Downs

ALTER TABLE participations DROP CONSTRAINT unique_user_event;


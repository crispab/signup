# --- First database schema
# --- For heroku deployment - use PostgreSQL - see http://www.postgresql.org/
# --- Must also work with H2 - test locally
# ---
# --- Password is 'admin'

# --- !Ups

INSERT INTO users (id, first_name, last_name, comment, email, phone, permission, pwd)
  VALUES (- 6, 'Admin', 'Istratör', 'Administratör för SignUp4 ', 'admin@crisp.se', '08-55695015', 'Administrator', '21232f297a57a5a743894a0e4a801fc3');



# --- !Downs

DELETE FROM users
WHERE id = -6;

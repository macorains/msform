# --- !Ups
ALTER TABLE M_USERINFO
ADD COLUMN DELETABLE TINYINT AFTER ACTIVATED

# --- !Downs
ALTER TABLE M_USERINFO DROP COLUMN DELETABLE;
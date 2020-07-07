# --- !Ups
CREATE TABLE D_FORM_COL (
  ID INT NOT NULL AUTO_INCREMENT,
  FORM_ID INT NOT NULL,
  NAME VARCHAR(255) NOT NULL,
  COL_ID VARCHAR(45) NOT NULL,
  COL_INDEX INT NOT NULL,
  COL_TYPE INT NOT NULL,
  DEFAULT_VALUE VARCHAR(255) NULL,
  USER_GROUP VARCHAR(30) NOT NULL,
  CREATED_USER VARCHAR(45) NOT NULL,
  MODIFIED_USER VARCHAR(45) NOT NULL,
  CREATED VARCHAR(45) NOT NULL,
  MODIFIED VARCHAR(45) NOT NULL,
  PRIMARY KEY (ID, FORM_ID),
  INDEX IDX_USER_GROUP (USER_GROUP ASC) VISIBLE);

# --- !Downs
DROP TABLE D_FORM_COL;
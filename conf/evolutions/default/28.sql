# --- !Ups
CREATE TABLE D_TRANSFER_CONFIG (
  ID INT NOT NULL AUTO_INCREMENT,
  TYPE_CODE VARCHAR(45) NOT NULL,
  CONFIG_INDEX INT NOT NULL,
  NAME VARCHAR(255) NOT NULL,
  STATUS INT NOT NULL,
  USER_GROUP VARCHAR(30) NOT NULL,
  CREATED_USER VARCHAR(45) NOT NULL,
  MODIFIED_USER VARCHAR(45) NOT NULL,
  CREATED DATETIME NOT NULL,
  MODIFIED DATETIME NOT NULL,
  PRIMARY KEY (ID),
  INDEX IDX_USER_GROUP (USER_GROUP ASC));

CREATE TABLE D_FORM_TRANSFER_TASK (
  ID INT NOT NULL,
  TRANSFER_CONFIG_ID INT NOT NULL,
  FORM_ID INT NOT NULL,
  TASK_INDEX INT NOT NULL,
  USER_GROUP VARCHAR(30) NOT NULL,
  CREATED_USER VARCHAR(45) NOT NULL,
  MODIFIED_USER VARCHAR(45) NOT NULL,
  CREATED DATETIME NOT NULL,
  MODIFIED DATETIME NOT NULL,
  PRIMARY KEY (ID),
  INDEX IDX_FORM_ID (FORM_ID ASC) VISIBLE);
  
CREATE TABLE D_FORM_TRANSFER_TASK_CONDITION (
  ID INT NOT NULL,
  FORM_TRANSFER_TASK_ID INT NOT NULL,
  FORM_ID INT NOT NULL,
  FORM_COL_ID INT NOT NULL,
  OPERATOR VARCHAR(10) NOT NULL,
  COND_VALUE VARCHAR(255) NOT NULL,
  USER_GROUP VARCHAR(30) NOT NULL,
  CREATED_USER VARCHAR(45) NOT NULL,
  MODIFIED_USER VARCHAR(45) NOT NULL,
  CREATED DATETIME NOT NULL,
  MODIFIED DATETIME NOT NULL,
  PRIMARY KEY (ID),
  INDEX IDX_FORM_TRANSFER_TASK_ID (FORM_TRANSFER_TASK_ID ASC));

# --- !Downs
DROP TABLE D_TRANSFER_CONFIG;
DROP TABLE D_FORM_TRANSFER_TASK;
DROP TABLE D_FORM_TRANSFER_TASK_CONDITION;
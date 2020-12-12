# --- !Ups

CREATE TABLE `D_FORM` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `HASHED_ID` varchar(45) NOT NULL,
  `FORM_DATA` json,
  `CREATED_USER` varchar(45),  
  `MODIFIED_USER` varchar(45),
  `CREATED` datetime DEFAULT NULL,
  `MODIFIED` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table D_FORM;

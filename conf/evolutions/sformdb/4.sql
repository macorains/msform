# --- !Ups

CREATE TABLE `D_AUTHTOKEN` (
  `ID` varchar(50) NOT NULL,
  `USER_ID` varchar(50) NOT NULL,
  `EXPIRY` datetime
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table D_AUTHTOKEN;

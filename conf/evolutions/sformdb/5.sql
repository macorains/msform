# --- !Ups

CREATE TABLE `M_USERINFO` (
  `USER_ID` varchar(50) NOT NULL,
  `PROVIDER_ID` varchar(50),
  `PROVIDER_KEY` varchar(50),
  `FIRST_NAME` varchar(30),
  `LAST_NAME` varchar(30),
  `FULL_NAME` varchar(50),
  `EMAIL` varchar(255),
  `AVATAR_URL` varchar(255),
  `ACTIVATED` tinyint,
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table M_USERINFO;

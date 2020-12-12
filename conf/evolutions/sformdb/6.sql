# --- !Ups

CREATE TABLE `M_AUTHINFO` (
  `AUTHINFO_ID` int(11) NOT NULL AUTO_INCREMENT,
  `PROVIDER_ID` varchar(50) NOT NULL,
  `PROVIDER_KEY` varchar(50) NOT NULL,
  `HASHER` varchar(255),
  `PASSWORD` varchar(255),
  `SALT` varchar(255),
  PRIMARY KEY (`AUTHINFO_ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
  drop table M_AUTHINFO;

# --- !Ups
CREATE TABLE `M_TRANSFERS` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TYPE_ID` int(11),
  `NAME` varchar(128) NOT NULL,
  `STATUS` int(11) DEFAULT 1,
  `CONFIG` json,
  `CREATED` datetime DEFAULT NULL,
  `MODIFIED` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table M_TRANSFERS;


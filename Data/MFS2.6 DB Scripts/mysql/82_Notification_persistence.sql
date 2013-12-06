

DROP TABLE IF EXISTS notification_log;

CREATE TABLE notification_log (
       ID BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
       Version INT(11) NOT NULL,
		   LastUpdateTime DATETIME NOT NULL,
       UpdatedBy VARCHAR(255) NOT NULL,
		   CreateTime DATETIME NOT NULL,
       CreatedBy VARCHAR(255) NOT NULL,
       SctlID bigint(20) NOT NULL,
       Code int(11) NOT NULL,
       Text varchar(255) DEFAULT NULL,
       PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
  
  
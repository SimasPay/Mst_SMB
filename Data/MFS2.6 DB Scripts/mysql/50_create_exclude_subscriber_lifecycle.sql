
CREATE TABLE exclude_subscriber_lifecycle (
         ID BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
         Version INT(11) NOT NULL,
		 LastUpdateTime DATETIME NOT NULL,
         UpdatedBy VARCHAR(255) NOT NULL,
		 CreateTime DATETIME NOT NULL,
         CreatedBy VARCHAR(255) NOT NULL,
		 MDNID BIGINT(20) NOT NULL,
		 PRIMARY KEY (`ID`),
		 Foreign Key (`MDNID`) references subscriber_mdn(`ID`)
       )ENGINE=InnoDB DEFAULT CHARSET=latin1;


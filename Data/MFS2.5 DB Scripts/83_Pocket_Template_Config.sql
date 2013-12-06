
use mfino;

CREATE TABLE mfino.pocket_template_config (
         ID BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
         Version INT(11) NOT NULL,
		 LastUpdateTime DATETIME NOT NULL,
         UpdatedBy VARCHAR(255) NOT NULL,
		 CreateTime DATETIME NOT NULL,
         CreatedBy VARCHAR(255) NOT NULL,
		 SubscriberType INT(11) unsigned NOT NULL,
         BusinessPartnerType INT(11) unsigned,
		 KYCLevel BIGINT(20) NOT NULL,
         Commodity INT(11) unsigned NOT NULL,
		 PocketType INT(11) unsigned NOT NULL,
         IsSuspencePocket TINYINT(11) unsigned DEFAULT '0',
		 IsCollectorPocket TINYINT(11) unsigned DEFAULT '0',
         PocketTemplateID BIGINT(20) NOT NULL,
		 PRIMARY KEY (`ID`),
		 Foreign Key (`KYCLevel`) references kyc_level(`ID`),
		 Foreign Key (`PocketTemplateID`) references pocket_template(`ID`)
       )ENGINE=InnoDB DEFAULT CHARSET=latin1;


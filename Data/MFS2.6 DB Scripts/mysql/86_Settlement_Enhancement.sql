

DROP TABLE IF EXISTS schedule_template;

CREATE TABLE schedule_template (
       ID BIGINT(20) NOT NULL AUTO_INCREMENT,
       Version INT(11) NOT NULL,
		   LastUpdateTime DATETIME NOT NULL,
       UpdatedBy VARCHAR(255) NOT NULL,
		   CreateTime DATETIME NOT NULL,
       CreatedBy VARCHAR(255) NOT NULL,
       Name VARCHAR(255) NOT NULL,
       ModeType VARCHAR(255),
       TimeType VARCHAR(255),
       DayOfWeek INT(11),
       DayOfMonth VARCHAR(255),
       Cron VARCHAR(255) NOT NULL,
       MSPID bigint(20) not null,
       PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sctl_settlement_map;

CREATE TABLE sctl_settlement_map (
       ID BIGINT(20) NOT NULL AUTO_INCREMENT,
       Version INT(11) NOT NULL,
		   LastUpdateTime DATETIME NOT NULL,
       UpdatedBy VARCHAR(255) NOT NULL,
		   CreateTime DATETIME NOT NULL,
       CreatedBy VARCHAR(255) NOT NULL,      
       Amount DECIMAL(25,4) NOT NULL,
       Status INT(11) NOT NULL,
       StlID BIGINT(20) DEFAULT NULL,
       SctlId BIGINT(20) NOT NULL,
       PartnerID BIGINT(20) NOT NULL,
       ServiceID BIGINT(20) NOT NULL,       
       MSPID bigint(20) not null,
       PRIMARY KEY (`ID`),
       Foreign Key (`StlID`) references settlement_txn_log(`ID`),
       Foreign Key (`SctlId`) references service_charge_txn_log(`ID`),
       Foreign Key (`PartnerID`) references partner(`ID`),
       Foreign Key (`ServiceID`) references service(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS settlement_txn_sctl_map;

CREATE TABLE settlement_txn_sctl_map (
       ID BIGINT(20) NOT NULL AUTO_INCREMENT,
       Version INT(11) NOT NULL,
		   LastUpdateTime DATETIME NOT NULL,
       UpdatedBy VARCHAR(255) NOT NULL,
		   CreateTime DATETIME NOT NULL,
       CreatedBy VARCHAR(255) NOT NULL,       
       Status INT(11) NOT NULL,
       SctlId BIGINT(20) NOT NULL,
       StlID BIGINT(20) NOT NULL,       
       MSPID bigint(20) not null,
       PRIMARY KEY (`ID`),
       Foreign Key (`StlID`) references settlement_txn_log(`ID`),
       Foreign Key (`SctlId`) references service_charge_txn_log(`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO schedule_template 
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, TimeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  ('1','1', NOW(), 'system', NOW(), 'system', 'Daily','Daily','Daily',1,'3','0 0/5 * * * ?', 1);


ALTER TABLE settlement_template ADD ScheduleTemplateID BIGINT(20) NOT NULL DEFAULT '1';
ALTER TABLE settlement_template ADD CONSTRAINT FK_setlmnt_tmplt_schdl  FOREIGN KEY (ScheduleTemplateID) REFERENCES schedule_template(`ID`);

ALTER TABLE settlement_txn_log ADD Amount DECIMAL(25,4),
        ADD Description VARCHAR(255);
        

# Migration of settlement_template

UPDATE settlement_template SET ScheduleTemplateID = 1;


 
 
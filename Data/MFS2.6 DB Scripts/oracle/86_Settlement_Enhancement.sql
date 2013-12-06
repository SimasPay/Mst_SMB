CREATE TABLE schedule_template (
       ID NUMBER(19,0) NOT NULL PRIMARY KEY,
       Version Number(10,0) NOT NULL,
		   LastUpdateTime TIMESTAMP NOT NULL,
       UpdatedBy varchar2(255) NOT NULL,
       CreateTime TIMESTAMP NOT NULL,
       CreatedBy varchar2(255) NOT NULL,
       Name varchar2(255) NOT NULL,
       ModeType varchar2(255),
       TimeType varchar2(255),
       DayOfWeek Number(10,0),
       DayOfMonth varchar2(255),
       Cron varchar2(255) NOT NULL,
       MSPID NUMBER(19,0) NOT NULL
);

CREATE SEQUENCE  schedule_template_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

CREATE TABLE sctl_settlement_map (
       ID NUMBER(19,0) NOT NULL PRIMARY KEY,
       Version Number(10,0) NOT NULL,
		   LastUpdateTime TIMESTAMP NOT NULL,
       UpdatedBy varchar2(255) NOT NULL,
       CreateTime TIMESTAMP NOT NULL,
       CreatedBy varchar2(255) NOT NULL,       
       Amount DECIMAL(25,4) NOT NULL,
       Status Number(10,0) NOT NULL,
       StlID NUMBER(19,0) DEFAULT NULL,
       SctlId NUMBER(19,0) NOT NULL,
       PartnerID NUMBER(19,0) NOT NULL,
       ServiceID NUMBER(19,0) NOT NULL,       
       MSPID NUMBER(19,0) NOT NULL
);

CREATE SEQUENCE  sctl_settlement_map_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

ALTER TABLE sctl_settlement_map
ADD CONSTRAINT FK_stlmtmap_schedule_template FOREIGN KEY
(
  StlID
)
REFERENCES settlement_txn_log
(
  ID
)
ENABLE
;
ALTER TABLE sctl_settlement_map
ADD CONSTRAINT FK_stlmtmap_sctl FOREIGN KEY
(
  SctlId
)
REFERENCES service_charge_txn_log
(
  ID
)
ENABLE
;
ALTER TABLE sctl_settlement_map
ADD CONSTRAINT FK_stlmtmap_partner FOREIGN KEY
(
  PartnerID
)
REFERENCES partner
(
  ID
)
ENABLE
;
ALTER TABLE sctl_settlement_map
ADD CONSTRAINT FK_stlmtmap_service FOREIGN KEY
(
  ServiceID
)
REFERENCES service
(
  ID
)
ENABLE
;


CREATE TABLE settlement_txn_sctl_map (
       ID NUMBER(19,0) NOT NULL PRIMARY KEY,
       Version Number(10,0) NOT NULL,
		   LastUpdateTime TIMESTAMP NOT NULL,
       UpdatedBy varchar2(255) NOT NULL,
       CreateTime TIMESTAMP NOT NULL,
       CreatedBy varchar2(255) NOT NULL,       
       Status Number(10,0) NOT NULL,
       SctlId NUMBER(19,0) NOT NULL,
       StlID NUMBER(19,0) NOT NULL,       
       MSPID NUMBER(19,0) not null
);

CREATE SEQUENCE  settlement_txn_sctl_map_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

ALTER TABLE settlement_txn_sctl_map
ADD CONSTRAINT FK_stlmtsctlmap_stl FOREIGN KEY
(
  StlID
)
REFERENCES settlement_txn_log
(
  ID
)
ENABLE
;
ALTER TABLE settlement_txn_sctl_map
ADD CONSTRAINT FK_stlmtsctlmap_sctl FOREIGN KEY
(
  SctlId
)
REFERENCES service_charge_txn_log
(
  ID
)
ENABLE
;

INSERT INTO schedule_template 
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, TimeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  (1,1, sysdate, 'system', sysdate, 'system', 'Daily','Daily','Daily',1,'3','0 0/5 * * * ?', 1);
  

ALTER TABLE settlement_template ADD ScheduleTemplateID NUMBER(19,0) DEFAULT 1 NOT NULL ;

ALTER TABLE settlement_template
ADD CONSTRAINT FK_setlmnt_tmplt_schdl FOREIGN KEY
(
  ScheduleTemplateID
)
REFERENCES schedule_template
(
  ID
)
ENABLE
;

ALTER TABLE settlement_txn_log ADD Amount NUMBER(25,4);
ALTER TABLE settlement_txn_log ADD Description varchar2(255);

-- Migration of settlement_template

UPDATE settlement_template SET ScheduleTemplateID = 1;

commit;
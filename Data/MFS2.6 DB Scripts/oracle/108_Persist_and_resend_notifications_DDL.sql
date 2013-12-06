
ALTER TABLE notification_log ADD NotificationMethod NUMBER(10,0);

ALTER TABLE notification_log ADD SourceAddress VARCHAR2(255 CHAR);
 
ALTER TABLE notification_log ADD NotificationReceiverType NUMBER(10,0);

ALTER TABLE notification_log ADD EmailSubject VARCHAR2(255 CHAR);


DROP TABLE notification_log_details CASCADE CONSTRAINTS;

CREATE TABLE notification_log_details (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  NotificationLogID NUMBER(19,0) NOT NULL,
  Status NUMBER(10,0) NOT NULL,
  CONSTRAINT FK_Notification_Log_ID
  FOREIGN KEY (NotificationLogID)
  REFERENCES notification_log(ID)
);

PROMPT Creating Primary Key Constraint notification_log_details_pk on table notification_log_details ... 
ALTER TABLE notification_log_details
ADD CONSTRAINT notification_log_details_pk PRIMARY KEY
(
  ID
)
ENABLE
;

CREATE SEQUENCE  NLD_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

commit;
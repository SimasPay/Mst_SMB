CREATE TABLE subscriber_status_event (
  id               NUMBER(19) not null,
  version          NUMBER(10) not null,
  lastupdatetime   TIMESTAMP(0) not null,
  updatedby        VARCHAR2(255 CHAR) default ' ' not null,
  createtime       TIMESTAMP(0) not null,
  createdby        VARCHAR2(255 CHAR) not null,
  subscriberid     NUMBER(19) not null,
  pickupdatetime   TIMESTAMP(0) not null,
  processingstatus NUMBER(3) default '0',
  statusonpickup   NUMBER(10,0) NOT NULL,
  subscribertype   NUMBER(10,0) NOT NULL
);


ALTER TABLE subscriber_status_event
ADD CONSTRAINT PRIMARY_Subscriber_event PRIMARY KEY
(
  ID
)
ENABLE
;

CREATE SEQUENCE  subscriber_status_event_ID_SEQ  
MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;


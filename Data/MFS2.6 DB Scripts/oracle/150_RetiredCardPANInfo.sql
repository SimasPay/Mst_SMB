DROP TABLE retired_cardpan_info CASCADE CONSTRAINTS;
CREATE TABLE retired_cardpan_info (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  CardPAN VARCHAR2(255 CHAR) NOT NULL,
  RetireCount NUMBER(10,0) NOT NULL,
  CONSTRAINT UNIQUE_retired_cardpan_info UNIQUE (CardPAN)  
  );

PROMPT Creating Primary Key Constraint retired_cardpan_info_pk on table retired_cardpan_info ...
ALTER TABLE retired_cardpan_info
ADD CONSTRAINT retired_cardpan_info_pk PRIMARY KEY
(
  ID
)
ENABLE
;

DROP SEQUENCE  retired_cardpan_info_ID_SEQ;
CREATE SEQUENCE  retired_cardpan_info_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

commit;

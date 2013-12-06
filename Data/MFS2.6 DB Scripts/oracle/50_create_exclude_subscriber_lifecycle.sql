

PROMPT Creating Table exclude_subscriber_lifecycle ...
CREATE TABLE exclude_subscriber_lifecycle (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  MDNID NUMBER(19,0) NOT NULL 
);


PROMPT Creating Primary Key Constraint exclude_sub_lc_pk on table exclude_subscriber_lifecycle ... 
ALTER TABLE exclude_subscriber_lifecycle
ADD CONSTRAINT exclude_sub_lc_pk PRIMARY KEY
(
  ID
)
ENABLE
;

PROMPT Creating Foreign Key Constraint FK_exclude_sub_lc on table exclude_subscriber_lifecycle...
ALTER TABLE exclude_subscriber_lifecycle
ADD CONSTRAINT FK_exclude_sub_lc_MDNID FOREIGN KEY
(
  MDNID
)
REFERENCES subscriber_mdn
(
  ID
)
ENABLE
;

commit;
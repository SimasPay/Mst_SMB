create table integration_partner_map (
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  InstitutionID varchar2(255) NOT NULL,
  PartnerID Number(19,0) NOT NULL     
);

ALTER TABLE INTEGRATION_PARTNER_MAP ADD CONSTRAINT "FK_INTEGRATION_PARTNER_MAP_PA" FOREIGN KEY ("PARTNERID")
	  REFERENCES PARTNER ("ID") ENABLE;

CREATE SEQUENCE  integration_partner_map_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
  
  
 commit;
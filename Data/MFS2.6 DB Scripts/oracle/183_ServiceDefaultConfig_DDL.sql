CREATE TABLE service_defualt_config (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  ServiceProviderID number(19,0) NOT NULL,
  ServiceID number(19,0) NOT NULL,
  SourcePocketType number(10,0) DEFAULT 1,
  DestPocketType number(10,0) DEFAULT 3,
  CONSTRAINT FK_ServiceDefaultConfiguration_PartnerByServiceProviderID FOREIGN KEY (ServiceProviderID) REFERENCES partner(ID),
  CONSTRAINT FK_ServiceDefaultConfiguration_Service FOREIGN KEY (ServiceID) REFERENCES service(ID)
);

CREATE SEQUENCE service_defualt_config_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1 NOCYCLE ; 

commit; 
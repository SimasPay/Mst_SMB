CREATE TABLE partner_default_services (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  BusinessPartnerType number(10,0) NOT NULL,
  ServiceDefaultConfigurationID number(19,0) NOT NULL,
  CONSTRAINT FK_PartnerDefaultServices_ServiceDefaultConfiguration FOREIGN KEY (ServiceDefaultConfigurationID) REFERENCES service_defualt_config(ID)
);

CREATE SEQUENCE partner_default_services_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1 NOCYCLE ; 

commit; 